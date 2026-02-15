import { useState, useEffect } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import styled from 'styled-components'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { LCARSDataDisplay } from '../components/lcars/LCARSDataDisplay'
import { LCARSColumn } from '../components/lcars/LCARSColumn'
import { LCARSAlert } from '../components/lcars/LCARSAlert'
import { useMaintenanceTemplate, useCreateMaintenanceTemplate, useUpdateMaintenanceTemplate } from '../hooks/useMaintenance'
import { useBoats } from '../hooks/useBoats'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'


const Container = styled.div`
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 20px;
  height: 100vh;
  padding: 20px;
`

const MainContent = styled.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
`

const ContentArea = styled(LCARSPanel)`
  flex: 1;
  overflow-y: auto;
  padding: 20px;
`

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 20px;
`

const FormSection = styled.div`
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 20px;
  background-color: ${props => props.theme.colors.background}40;
  border-radius: 4px;
  border-left: 4px solid ${props => props.theme.colors.primary.neonCarrot};
`

const SectionTitle = styled.h3`
  color: ${props => props.theme.colors.primary.neonCarrot};
  margin: 0 0 10px 0;
  font-size: 16px;
  text-transform: uppercase;
`

const FormRow = styled.div`
  display: flex;
  gap: 15px;
  align-items: flex-start;
`

const FormLabel = styled.label`
  color: ${props => props.theme.colors.text.secondary};
  font-weight: bold;
  min-width: 150px;
  padding-top: 8px;
`

const FormInput = styled.input`
  background-color: ${props => props.theme.colors.background};
  color: ${props => props.theme.colors.text.primary};
  border: 2px solid ${props => props.theme.colors.primary.neonCarrot};
  padding: 8px 12px;
  border-radius: 4px;
  font-family: inherit;
  flex: 1;

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.anakiwa};
  }
`

const FormSelect = styled.select`
  background-color: ${props => props.theme.colors.background};
  color: ${props => props.theme.colors.text.primary};
  border: 2px solid ${props => props.theme.colors.primary.neonCarrot};
  padding: 8px 12px;
  border-radius: 4px;
  font-family: inherit;
  flex: 1;

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.anakiwa};
  }
`

const FormTextarea = styled.textarea`
  background-color: ${props => props.theme.colors.background};
  color: ${props => props.theme.colors.text.primary};
  border: 2px solid ${props => props.theme.colors.primary.neonCarrot};
  padding: 8px 12px;
  border-radius: 4px;
  font-family: inherit;
  resize: vertical;
  min-height: 80px;
  flex: 1;

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.anakiwa};
  }
`

const FormCheckbox = styled.input`
  margin-right: 8px;
`

const RecurrenceRow = styled.div`
  display: flex;
  gap: 10px;
  align-items: center;
  flex: 1;
`

const ActionBar = styled.div`
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid ${props => props.theme.colors.primary.neonCarrot}40;
`

const LoadingContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: ${props => props.theme.colors.primary.neonCarrot};
  font-size: 18px;
`

const ErrorContainer = styled.div`
  padding: 20px;
  text-align: center;
`

interface FormData {
  boatId: string
  title: string
  description: string
  component: string
  hasRecurrence: boolean
  recurrenceType: 'days' | 'weeks' | 'months' | 'years' | 'engine_hours'
  recurrenceInterval: string
  estimatedCost: string
  estimatedTime: string
  isActive: boolean
}

export function MaintenanceTemplateForm() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const isEditing = !!id

  const [formData, setFormData] = useState<FormData>({
    boatId: '',
    title: '',
    description: '',
    component: '',
    hasRecurrence: false,
    recurrenceType: 'days',
    recurrenceInterval: '30',
    estimatedCost: '',
    estimatedTime: '',
    isActive: true
  })

  const { data: boats = [] } = useBoats()
  const { data: template, isLoading: templateLoading } = useMaintenanceTemplate(id!, { enabled: isEditing })
  const createMutation = useCreateMaintenanceTemplate()
  const updateMutation = useUpdateMaintenanceTemplate()

  useEffect(() => {
    if (template && isEditing) {
      setFormData({
        boatId: template.boatId,
        title: template.title,
        description: template.description || '',
        component: template.component || '',
        hasRecurrence: !!template.recurrence,
        recurrenceType: template.recurrence?.type || 'days',
        recurrenceInterval: template.recurrence?.interval?.toString() || '30',
        estimatedCost: template.estimatedCost?.toString() || '',
        estimatedTime: template.estimatedTime?.toString() || '',
        isActive: template.isActive
      })
    }
  }, [template, isEditing])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!formData.boatId || !formData.title) {
      alert('Please fill in all required fields (Boat and Title)')
      return
    }

    try {
      const submitData: any = {
        boatId: formData.boatId,
        title: formData.title,
        description: formData.description || undefined,
        component: formData.component || undefined,
        estimatedCost: formData.estimatedCost ? parseFloat(formData.estimatedCost) : undefined,
        estimatedTime: formData.estimatedTime ? parseInt(formData.estimatedTime) : undefined,
      }

      if (formData.hasRecurrence) {
        submitData.recurrence = {
          type: formData.recurrenceType,
          interval: parseInt(formData.recurrenceInterval)
        }
      }

      if (isEditing) {
        submitData.isActive = formData.isActive
        await updateMutation.mutateAsync({ id: id!, data: submitData })
      } else {
        await createMutation.mutateAsync(submitData)
      }

      navigate('/maintenance')
    } catch (error) {
      console.error('Failed to save template:', error)
      alert('Failed to save maintenance template. Please try again.')
    }
  }

  const handleInputChange = (field: keyof FormData, value: string | boolean) => {
    setFormData(prev => ({ ...prev, [field]: value }))
  }

  if (isEditing && templateLoading) {
    return (
      <Container>
        <LCARSColumn>
          <LCARSDataDisplay label="Status" value="LOADING" />
        </LCARSColumn>
        <MainContent>
          <LCARSHeader>Edit Maintenance Template</LCARSHeader>
          <ContentArea>
            <LoadingContainer>Loading template...</LoadingContainer>
          </ContentArea>
        </MainContent>
      </Container>
    )
  }

  if (isEditing && !template) {
    return (
      <Container>
        <LCARSColumn>
          <LCARSDataDisplay label="Status" value="ERROR" />
        </LCARSColumn>
        <MainContent>
          <LCARSHeader>Edit Maintenance Template</LCARSHeader>
          <ContentArea>
            <ErrorContainer>
              <LCARSAlert type="error">
                Template not found.
              </LCARSAlert>
              <Link to="/maintenance">
                <LCARSButton>Back to Maintenance</LCARSButton>
              </Link>
            </ErrorContainer>
          </ContentArea>
        </MainContent>
      </Container>
    )
  }

  const isSubmitting = createMutation.isPending || updateMutation.isPending

  return (
    <Container>
      <LCARSColumn>
        <LCARSDataDisplay label="Mode" value={isEditing ? "EDIT" : "CREATE"} />
        <LCARSDataDisplay label="Boats Available" value={boats.length.toString()} />
        {isEditing && template && (
          <LCARSDataDisplay label="Template Status" value={template.isActive ? "ACTIVE" : "INACTIVE"} />
        )}
      </LCARSColumn>

      <MainContent>
        <LCARSHeader>
          {isEditing ? 'Edit Maintenance Template' : 'Create Maintenance Template'}
        </LCARSHeader>

        <ContentArea>
          <Form onSubmit={handleSubmit}>
            <FormSection>
              <SectionTitle>Basic Information</SectionTitle>
              
              <FormRow>
                <FormLabel>Boat *</FormLabel>
                <FormSelect
                  value={formData.boatId}
                  onChange={(e) => handleInputChange('boatId', e.target.value)}
                  required
                >
                  <option value="">Select a boat</option>
                  {boats.map(boat => (
                    <option key={boat.id} value={boat.id}>{boat.name}</option>
                  ))}
                </FormSelect>
              </FormRow>

              <FormRow>
                <FormLabel>Title *</FormLabel>
                <FormInput
                  type="text"
                  value={formData.title}
                  onChange={(e) => handleInputChange('title', e.target.value)}
                  placeholder="e.g., Oil Change, Hull Cleaning, Engine Service"
                  required
                />
              </FormRow>

              <FormRow>
                <FormLabel>Component</FormLabel>
                <FormInput
                  type="text"
                  value={formData.component}
                  onChange={(e) => handleInputChange('component', e.target.value)}
                  placeholder="e.g., Engine, Hull, Electrical, Plumbing"
                />
              </FormRow>

              <FormRow>
                <FormLabel>Description</FormLabel>
                <FormTextarea
                  value={formData.description}
                  onChange={(e) => handleInputChange('description', e.target.value)}
                  placeholder="Detailed description of the maintenance task, including any special instructions or requirements"
                />
              </FormRow>
            </FormSection>

            <FormSection>
              <SectionTitle>Schedule</SectionTitle>
              
              <FormRow>
                <FormLabel>Recurring Task</FormLabel>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <FormCheckbox
                    type="checkbox"
                    checked={formData.hasRecurrence}
                    onChange={(e) => handleInputChange('hasRecurrence', e.target.checked)}
                  />
                  <span>This is a recurring maintenance task</span>
                </div>
              </FormRow>

              {formData.hasRecurrence && (
                <FormRow>
                  <FormLabel>Recurrence</FormLabel>
                  <RecurrenceRow>
                    <span>Every</span>
                    <FormInput
                      type="number"
                      min="1"
                      value={formData.recurrenceInterval}
                      onChange={(e) => handleInputChange('recurrenceInterval', e.target.value)}
                      style={{ width: '80px', flex: 'none' }}
                    />
                    <FormSelect
                      value={formData.recurrenceType}
                      onChange={(e) => handleInputChange('recurrenceType', e.target.value as any)}
                      style={{ flex: 'none', minWidth: '120px' }}
                    >
                      <option value="days">Days</option>
                      <option value="weeks">Weeks</option>
                      <option value="months">Months</option>
                      <option value="years">Years</option>
                      <option value="engine_hours">Engine Hours</option>
                    </FormSelect>
                  </RecurrenceRow>
                </FormRow>
              )}
            </FormSection>

            <FormSection>
              <SectionTitle>Estimates</SectionTitle>
              
              <FormRow>
                <FormLabel>Estimated Cost ($)</FormLabel>
                <FormInput
                  type="number"
                  step="0.01"
                  min="0"
                  value={formData.estimatedCost}
                  onChange={(e) => handleInputChange('estimatedCost', e.target.value)}
                  placeholder="0.00"
                />
              </FormRow>

              <FormRow>
                <FormLabel>Estimated Time (minutes)</FormLabel>
                <FormInput
                  type="number"
                  min="0"
                  value={formData.estimatedTime}
                  onChange={(e) => handleInputChange('estimatedTime', e.target.value)}
                  placeholder="60"
                />
              </FormRow>
            </FormSection>

            {isEditing && (
              <FormSection>
                <SectionTitle>Status</SectionTitle>
                
                <FormRow>
                  <FormLabel>Template Status</FormLabel>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <FormCheckbox
                      type="checkbox"
                      checked={formData.isActive}
                      onChange={(e) => handleInputChange('isActive', e.target.checked)}
                    />
                    <span>Template is active (generates future events)</span>
                  </div>
                </FormRow>
              </FormSection>
            )}

            <ActionBar>
              <Link to="/maintenance">
                <LCARSButton type="button">Cancel</LCARSButton>
              </Link>
              <ReadOnlyGuard>
                <LCARSButton
                  type="submit"
                  disabled={isSubmitting}
                  variant="accent"
                >
                  {isSubmitting ? 'Saving...' : (isEditing ? 'Update Template' : 'Create Template')}
                </LCARSButton>
              </ReadOnlyGuard>
            </ActionBar>
          </Form>
        </ContentArea>
      </MainContent>
    </Container>
  )
}