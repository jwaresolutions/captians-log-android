import React, { useState } from 'react'
import styled from 'styled-components'
import { useNavigate } from 'react-router-dom'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { NauticalProviderMeta } from '../types/nautical'
import { freeProviders, paidProviders } from '../config/nauticalProviders'
import { useNauticalSettings } from '../hooks/useNauticalSettings'

const PageContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.lg};
  max-width: 1200px;
  margin: 0 auto;
`

const BackRow = styled.div`
  display: flex;
  align-items: center;
  gap: ${props => props.theme.spacing.md};
`

const ProvidersGrid = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.md};
`

const ProviderCard = styled.div<{ $expanded: boolean }>`
  background: ${props => props.theme.colors.surface.dark};
  border: 1px solid ${props => props.theme.colors.surface.medium};
  border-radius: ${props => props.theme.borderRadius.sm};
  overflow: hidden;
`

const ProviderHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: ${props => props.theme.spacing.md};
  cursor: pointer;

  &:hover {
    background: ${props => props.theme.colors.surface.medium};
  }
`

const ProviderInfo = styled.div`
  display: flex;
  align-items: center;
  gap: ${props => props.theme.spacing.md};
  flex: 1;
`

const ProviderName = styled.span`
  color: ${props => props.theme.colors.primary.anakiwa};
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
`

const Badge = styled.span<{ $tier: 'free' | 'paid' }>`
  padding: 2px 8px;
  border-radius: 9999px;
  font-size: ${props => props.theme.typography.fontSize.sm};
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
  background: ${props => props.$tier === 'free'
    ? props.theme.colors.status.success + '30'
    : props.theme.colors.primary.neonCarrot + '30'};
  color: ${props => props.$tier === 'free'
    ? props.theme.colors.status.success
    : props.theme.colors.primary.neonCarrot};
  border: 1px solid ${props => props.$tier === 'free'
    ? props.theme.colors.status.success
    : props.theme.colors.primary.neonCarrot};
`

const ShortDesc = styled.span`
  color: ${props => props.theme.colors.text.muted};
  font-size: ${props => props.theme.typography.fontSize.sm};
`

const ToggleSwitch = styled.div<{ $active: boolean }>`
  width: 48px;
  height: 24px;
  border-radius: 12px;
  background: ${props => props.$active
    ? props.theme.colors.status.success
    : props.theme.colors.surface.medium};
  border: 2px solid ${props => props.$active
    ? props.theme.colors.status.success
    : props.theme.colors.text.muted};
  position: relative;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;

  &::after {
    content: '';
    position: absolute;
    width: 18px;
    height: 18px;
    border-radius: 50%;
    background: ${props => props.theme.colors.text.primary};
    top: 1px;
    left: ${props => props.$active ? '24px' : '1px'};
    transition: left 0.2s ease;
  }
`

const ExpandedContent = styled.div`
  padding: 0 ${props => props.theme.spacing.md} ${props => props.theme.spacing.md};
  border-top: 1px solid ${props => props.theme.colors.surface.medium};
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.md};
`

const Description = styled.p`
  color: ${props => props.theme.colors.text.secondary};
  margin: ${props => props.theme.spacing.sm} 0 0;
  line-height: 1.5;
`

const ProConList = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: ${props => props.theme.spacing.md};

  @media (max-width: 600px) {
    grid-template-columns: 1fr;
  }
`

const ProConSection = styled.div``

const ProConTitle = styled.div<{ $type: 'pro' | 'con' }>`
  color: ${props => props.$type === 'pro'
    ? props.theme.colors.status.success
    : props.theme.colors.primary.neonCarrot};
  font-weight: bold;
  text-transform: uppercase;
  font-size: ${props => props.theme.typography.fontSize.sm};
  letter-spacing: 1px;
  margin-bottom: ${props => props.theme.spacing.xs};
`

const ProConItem = styled.div<{ $type: 'pro' | 'con' }>`
  color: ${props => props.theme.colors.text.secondary};
  font-size: ${props => props.theme.typography.fontSize.sm};
  padding: 2px 0;

  &::before {
    content: '${props => props.$type === 'pro' ? '+' : '-'}';
    color: ${props => props.$type === 'pro'
      ? props.theme.colors.status.success
      : props.theme.colors.primary.neonCarrot};
    margin-right: ${props => props.theme.spacing.xs};
    font-weight: bold;
  }
`

const ApiKeyInput = styled.input`
  background: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.text.primary};
  padding: ${props => props.theme.spacing.sm};
  font-family: ${props => props.theme.typography.fontFamily.monospace};
  font-size: ${props => props.theme.typography.fontSize.sm};
  width: 100%;
  box-sizing: border-box;

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.neonCarrot};
  }

  &::placeholder {
    color: ${props => props.theme.colors.text.muted};
  }
`

const ApiKeySection = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.xs};
`

const ApiKeyLabel = styled.label`
  color: ${props => props.theme.colors.primary.anakiwa};
  font-weight: bold;
  text-transform: uppercase;
  font-size: ${props => props.theme.typography.fontSize.sm};
  letter-spacing: 1px;
`

const LinkRow = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.md};
  align-items: center;
`

const ExternalLink = styled.a`
  color: ${props => props.theme.colors.primary.anakiwa};
  text-decoration: none;
  font-size: ${props => props.theme.typography.fontSize.sm};

  &:hover {
    color: ${props => props.theme.colors.primary.tanoi};
    text-decoration: underline;
  }
`

const PricingNote = styled.div`
  color: ${props => props.theme.colors.primary.lilac};
  font-size: ${props => props.theme.typography.fontSize.sm};
  font-style: italic;
`

const ProviderCardComponent: React.FC<{
  provider: NauticalProviderMeta
  enabled: boolean
  apiKey?: string
  onToggle: () => void
  onApiKeyChange: (key: string) => void
}> = ({ provider, enabled, apiKey, onToggle, onApiKeyChange }) => {
  const [expanded, setExpanded] = useState(false)

  return (
    <ProviderCard $expanded={expanded}>
      <ProviderHeader onClick={() => setExpanded(!expanded)}>
        <ProviderInfo>
          <ProviderName>{provider.name}</ProviderName>
          <Badge $tier={provider.tier}>{provider.tier}</Badge>
          {!expanded && <ShortDesc>{provider.description.split('.')[0]}</ShortDesc>}
        </ProviderInfo>
        <ToggleSwitch
          $active={enabled}
          onClick={(e) => {
            e.stopPropagation()
            onToggle()
          }}
        />
      </ProviderHeader>
      {expanded && (
        <ExpandedContent>
          <Description>{provider.description}</Description>

          <ProConList>
            <ProConSection>
              <ProConTitle $type="pro">Advantages</ProConTitle>
              {provider.pros.map((pro, i) => (
                <ProConItem key={i} $type="pro">{pro}</ProConItem>
              ))}
            </ProConSection>
            <ProConSection>
              <ProConTitle $type="con">Limitations</ProConTitle>
              {provider.cons.map((con, i) => (
                <ProConItem key={i} $type="con">{con}</ProConItem>
              ))}
            </ProConSection>
          </ProConList>

          {provider.requiresApiKey && (
            <ApiKeySection>
              <ApiKeyLabel>API Key</ApiKeyLabel>
              <ApiKeyInput
                type="password"
                placeholder="Enter API key..."
                value={apiKey || ''}
                onChange={(e) => onApiKeyChange(e.target.value)}
                onClick={(e) => e.stopPropagation()}
              />
              {provider.apiKeySignupUrl && (
                <ExternalLink href={provider.apiKeySignupUrl} target="_blank" rel="noopener noreferrer">
                  Get an API key →
                </ExternalLink>
              )}
            </ApiKeySection>
          )}

          <LinkRow>
            <ExternalLink href={provider.website} target="_blank" rel="noopener noreferrer">
              Visit website →
            </ExternalLink>
            {provider.pricingNote && (
              <PricingNote>{provider.pricingNote}</PricingNote>
            )}
          </LinkRow>
        </ExpandedContent>
      )}
    </ProviderCard>
  )
}

export const NauticalSettings: React.FC = () => {
  const navigate = useNavigate()
  const { isEnabled, getProviderConfig, toggleProvider, setApiKey } = useNauticalSettings()

  return (
    <PageContainer>
      <BackRow>
        <LCARSButton variant="secondary" size="sm" onClick={() => navigate('/settings')}>
          ← Settings
        </LCARSButton>
      </BackRow>

      <LCARSHeader>Nautical Data Providers</LCARSHeader>

      <LCARSPanel title="Free Providers" variant="primary">
        <ProvidersGrid>
          {freeProviders.map(provider => (
            <ProviderCardComponent
              key={provider.id}
              provider={provider}
              enabled={isEnabled(provider.id)}
              apiKey={getProviderConfig(provider.id).apiKey}
              onToggle={() => toggleProvider(provider.id)}
              onApiKeyChange={(key) => setApiKey(provider.id, key)}
            />
          ))}
        </ProvidersGrid>
      </LCARSPanel>

      <LCARSPanel title="Paid Providers" variant="secondary">
        <ProvidersGrid>
          {paidProviders.map(provider => (
            <ProviderCardComponent
              key={provider.id}
              provider={provider}
              enabled={isEnabled(provider.id)}
              apiKey={getProviderConfig(provider.id).apiKey}
              onToggle={() => toggleProvider(provider.id)}
              onApiKeyChange={(key) => setApiKey(provider.id, key)}
            />
          ))}
        </ProvidersGrid>
      </LCARSPanel>
    </PageContainer>
  )
}
