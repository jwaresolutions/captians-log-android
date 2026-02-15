import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import styled from 'styled-components'
import {
  LCARSHeader,
  LCARSPanel,
  LCARSButton,
  LCARSAlert
} from '../components/lcars'
import { useCreateBoat } from '../hooks/useBoats'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'

const Container = styled.div`
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
`

const FormPanel = styled(LCARSPanel)`
  padding: 30px;
  margin-top: 20px;
`

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 25px;
`

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
`

const Label = styled.label`
  color: ${props => props.theme.colors.text.primary};
  font-size: 1rem;
  text-transform: uppercase;
  font-weight: bold;
  font-family: ${props => props.theme.typography.fontFamily.primary};
`

const Input = styled.input`
  padding: 15px 20px;
  background: ${props => props.theme.colors.background};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.text.primary};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: 1.1rem;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 15px ${props => props.theme.colors.primary.neonCarrot}40;
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    border-color: ${props => props.theme.colors.interactive.disabled};
  }

  &::placeholder {
    color: ${props => props.theme.colors.text.secondary};
    opacity: 0.7;
  }
`

const TextArea = styled.textarea`
  padding: 15px 20px;
  background: ${props => props.theme.colors.background};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.text.primary};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: 1rem;
  min-height: 120px;
  resize: vertical;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 15px ${props => props.theme.colors.primary.neonCarrot}40;
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    border-color: ${props => props.theme.colors.interactive.disabled};
  }

  &::placeholder {
    color: ${props => props.theme.colors.text.secondary};
    opacity: 0.7;
  }
`

const HelpText = styled.p`
  color: ${props => props.theme.colors.text.secondary};
  font-size: 0.9rem;
  margin: 0;
  line-height: 1.4;
`

const FormActions = styled.div`
  display: flex;
  gap: 20px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 2px solid ${props => props.theme.colors.primary.anakiwa};
`

const BackButton = styled(LCARSButton)`
  margin-right: 15px;
`

const RequiredIndicator = styled.span`
  color: ${props => props.theme.colors.primary.neonCarrot};
  margin-left: 5px;
`

const ValidationError = styled.div`
  color: ${props => props.theme.colors.status.error};
  font-size: 0.9rem;
  margin-top: 5px;
`

const HeaderContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
`

const HeaderTitle = styled.div`
  display: flex;
  flex-direction: column;
  gap: 5px;
`

interface FormData {
  name: string
  description: string
  hullNumber: string
  manufacturer: string
  model: string
  year: string
  length: string
}

interface FormErrors {
  name?: string
  description?: string
  hullNumber?: string
  manufacturer?: string
  model?: string
  year?: string
  length?: string
}

export const BoatForm: React.FC = () => {
  const navigate = useNavigate()
  const createBoat = useCreateBoat()
  
  const [formData, setFormData] = useState<FormData>({
    name: '',
    description: '',
    hullNumber: '',
    manufacturer: '',
    model: '',
    year: '',
    length: ''
  })
  
  const [errors, setErrors] = useState<FormErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleBack = () => {
    navigate('/boats')
  }

  const handleInputChange = (field: keyof FormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }))
    
    // Clear error when user starts typing
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }))
    }
  }

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {}

    // Required field validation
    if (!formData.name.trim()) {
      newErrors.name = 'Vessel name is required'
    } else if (formData.name.trim().length < 2) {
      newErrors.name = 'Vessel name must be at least 2 characters'
    } else if (formData.name.trim().length > 100) {
      newErrors.name = 'Vessel name must be less than 100 characters'
    }

    // Optional field validation
    if (formData.description && formData.description.length > 500) {
      newErrors.description = 'Description must be less than 500 characters'
    }

    if (formData.hullNumber && formData.hullNumber.length > 50) {
      newErrors.hullNumber = 'Hull number must be less than 50 characters'
    }

    if (formData.manufacturer && formData.manufacturer.length > 100) {
      newErrors.manufacturer = 'Manufacturer must be less than 100 characters'
    }

    if (formData.model && formData.model.length > 100) {
      newErrors.model = 'Model must be less than 100 characters'
    }

    if (formData.year && (!/^\d{4}$/.test(formData.year) || parseInt(formData.year) < 1900 || parseInt(formData.year) > new Date().getFullYear() + 1)) {
      newErrors.year = 'Year must be a valid 4-digit year'
    }

    if (formData.length && (!/^\d+(\.\d+)?$/.test(formData.length) || parseFloat(formData.length) <= 0 || parseFloat(formData.length) > 1000)) {
      newErrors.length = 'Length must be a positive number (in feet)'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!validateForm()) {
      return
    }

    setIsSubmitting(true)
    
    try {
      // Prepare metadata with optional fields
      const metadata: Record<string, unknown> = {}
      
      if (formData.description.trim()) metadata.description = formData.description.trim()
      if (formData.hullNumber.trim()) metadata.hullNumber = formData.hullNumber.trim()
      if (formData.manufacturer.trim()) metadata.manufacturer = formData.manufacturer.trim()
      if (formData.model.trim()) metadata.model = formData.model.trim()
      if (formData.year.trim()) metadata.year = parseInt(formData.year.trim())
      if (formData.length.trim()) metadata.lengthFeet = parseFloat(formData.length.trim())

      const boat = await createBoat.mutateAsync({
        name: formData.name.trim(),
        metadata: Object.keys(metadata).length > 0 ? metadata : undefined
      })

      // Navigate to the new boat's detail page
      navigate(`/boats/${boat.id}`)
    } catch (error) {
      console.error('Failed to create boat:', error)
      // Error handling is done by the mutation hook and displayed via LCARSAlert
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <Container>
        <HeaderContainer>
          <HeaderTitle>
            <LCARSHeader>ADD NEW VESSEL</LCARSHeader>
            <HelpText>Register a new vessel for tracking</HelpText>
          </HeaderTitle>
          <BackButton variant="secondary" onClick={handleBack}>
            BACK TO VESSELS
          </BackButton>
        </HeaderContainer>

        {createBoat.error && (
          <LCARSAlert type="error">
            Failed to create vessel: {createBoat.error.message}
          </LCARSAlert>
        )}

        <FormPanel>
          <Form onSubmit={handleSubmit}>
            <FormGroup>
              <Label>
                Vessel Name
                <RequiredIndicator>*</RequiredIndicator>
              </Label>
              <Input
                type="text"
                value={formData.name}
                onChange={(e) => handleInputChange('name', e.target.value)}
                placeholder="Enter vessel name (e.g., 'Sea Explorer', 'Fishing Buddy')"
                disabled={isSubmitting}
                maxLength={100}
              />
              <HelpText>
                The primary name used to identify this vessel throughout the system.
              </HelpText>
              {errors.name && <ValidationError>{errors.name}</ValidationError>}
            </FormGroup>

            <FormGroup>
              <Label>Description</Label>
              <TextArea
                value={formData.description}
                onChange={(e) => handleInputChange('description', e.target.value)}
                placeholder="Optional description of the vessel (e.g., 'Center console fishing boat', '24ft cabin cruiser')"
                disabled={isSubmitting}
                maxLength={500}
              />
              <HelpText>
                Optional description to help identify and categorize this vessel.
              </HelpText>
              {errors.description && <ValidationError>{errors.description}</ValidationError>}
            </FormGroup>

            <FormGroup>
              <Label>Hull Identification Number (HIN)</Label>
              <Input
                type="text"
                value={formData.hullNumber}
                onChange={(e) => handleInputChange('hullNumber', e.target.value)}
                placeholder="Enter HIN if available"
                disabled={isSubmitting}
                maxLength={50}
              />
              <HelpText>
                The unique hull identification number assigned by the manufacturer.
              </HelpText>
              {errors.hullNumber && <ValidationError>{errors.hullNumber}</ValidationError>}
            </FormGroup>

            <FormGroup>
              <Label>Manufacturer</Label>
              <Input
                type="text"
                value={formData.manufacturer}
                onChange={(e) => handleInputChange('manufacturer', e.target.value)}
                placeholder="Enter manufacturer name"
                disabled={isSubmitting}
                maxLength={100}
              />
              <HelpText>
                The company that built this vessel.
              </HelpText>
              {errors.manufacturer && <ValidationError>{errors.manufacturer}</ValidationError>}
            </FormGroup>

            <FormGroup>
              <Label>Model</Label>
              <Input
                type="text"
                value={formData.model}
                onChange={(e) => handleInputChange('model', e.target.value)}
                placeholder="Enter model name"
                disabled={isSubmitting}
                maxLength={100}
              />
              <HelpText>
                The specific model designation of this vessel.
              </HelpText>
              {errors.model && <ValidationError>{errors.model}</ValidationError>}
            </FormGroup>

            <FormGroup>
              <Label>Year Built</Label>
              <Input
                type="text"
                value={formData.year}
                onChange={(e) => handleInputChange('year', e.target.value)}
                placeholder="Enter year (e.g., 2020)"
                disabled={isSubmitting}
                maxLength={4}
              />
              <HelpText>
                The year this vessel was manufactured.
              </HelpText>
              {errors.year && <ValidationError>{errors.year}</ValidationError>}
            </FormGroup>

            <FormGroup>
              <Label>Length (feet)</Label>
              <Input
                type="text"
                value={formData.length}
                onChange={(e) => handleInputChange('length', e.target.value)}
                placeholder="Enter length in feet (e.g., 24.5)"
                disabled={isSubmitting}
              />
              <HelpText>
                The overall length of the vessel in feet.
              </HelpText>
              {errors.length && <ValidationError>{errors.length}</ValidationError>}
            </FormGroup>

            <FormActions>
              <LCARSButton 
                type="button" 
                variant="secondary" 
                onClick={handleBack}
                disabled={isSubmitting}
              >
                CANCEL
              </LCARSButton>
              <ReadOnlyGuard>
                <LCARSButton
                  type="submit"
                  variant="primary"
                  disabled={isSubmitting || !formData.name.trim()}
                >
                  {isSubmitting ? 'CREATING VESSEL...' : 'CREATE VESSEL'}
                </LCARSButton>
              </ReadOnlyGuard>
            </FormActions>
          </Form>
        </FormPanel>
      </Container>
  )
}

export default BoatForm