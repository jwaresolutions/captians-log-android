import React from 'react'
import styled from 'styled-components'

interface TodoEmptyStateProps {
  title: string
  message: string
  icon?: string
}

const EmptyContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  text-align: center;
  gap: 16px;
`

const Icon = styled.div`
  font-size: 48px;
  line-height: 1;
  opacity: 0.6;
  filter: grayscale(0.3);
`

const Title = styled.h3`
  font-family: ${props => props.theme.typography.fontFamily};
  font-size: 20px;
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  color: ${props => props.theme.colors.text.primary};
  text-transform: uppercase;
  letter-spacing: ${props => props.theme.typography.letterSpacing};
  margin: 0;
`

const Message = styled.p`
  font-family: ${props => props.theme.typography.fontFamily};
  font-size: 14px;
  color: ${props => props.theme.colors.text.muted};
  text-transform: uppercase;
  letter-spacing: ${props => props.theme.typography.letterSpacing};
  margin: 0;
  max-width: 320px;
`

export const TodoEmptyState: React.FC<TodoEmptyStateProps> = ({ title, message, icon = '📋' }) => {
  return (
    <EmptyContainer>
      <Icon>{icon}</Icon>
      <Title>{title}</Title>
      <Message>{message}</Message>
    </EmptyContainer>
  )
}
