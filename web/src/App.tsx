import { Routes, Route } from 'react-router-dom'
import styled from 'styled-components'
import { LCARSLayout } from './components/lcars/LCARSLayout'
import { ErrorBoundary } from './components/ErrorBoundary'
import { Dashboard } from './pages/Dashboard'
import { HomePage } from './pages/HomePage'
import { SetupWizard } from './pages/SetupWizard'
import { BoatList } from './pages/BoatList'
import { BoatDetail } from './pages/BoatDetail'
import { BoatForm } from './pages/BoatForm'
import { TripList } from './pages/TripList'
import { TripDetail } from './pages/TripDetail'
import { TripEdit } from './pages/TripEdit'
import { TripForm } from './pages/TripForm'
import { NotesList } from './pages/NotesList'
import { NoteDetail } from './pages/NoteDetail'
import { NoteEditor } from './pages/NoteEditor'
import { TodoPage } from './pages/TodoPage'
import { MaintenanceList } from './pages/MaintenanceList'
import { MaintenanceTemplateDetail } from './pages/MaintenanceTemplateDetail'
import { MaintenanceEventDetail } from './pages/MaintenanceEventDetail'
import { MaintenanceTemplateForm } from './pages/MaintenanceTemplateForm'
import { MapView } from './pages/MapView'
import { LicenseProgress } from './pages/LicenseProgress'
import { MaintenanceReports } from './pages/MaintenanceReports'
import { Reports } from './pages/Reports'
import { Settings } from './pages/Settings'
import { NauticalSettings } from './pages/NauticalSettings'
import { BackupManager } from './pages/BackupManager'
import { Calendar } from './pages/Calendar'
import { PhotoGallery } from './pages/PhotoGallery'
import { Documentation } from './pages/Documentation'
import { useAuth } from './hooks/useAuth'

const AppContainer = styled.div`
  min-height: 100vh;
  background-color: ${props => props.theme.colors.background};
  color: ${props => props.theme.colors.text.primary};
`

const LoadingScreen = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: ${props => props.theme.colors.background};
  
  .loading-text {
    color: ${props => props.theme.colors.primary.neonCarrot};
    font-size: 24px;
    font-weight: bold;
    text-transform: uppercase;
    letter-spacing: 2px;
  }
`

function App() {
  const { isAuthenticated, isLoading, needsSetup } = useAuth()

  if (isLoading) {
    return (
      <AppContainer>
        <LoadingScreen>
          <div className="loading-text">Initializing LCARS Interface</div>
        </LoadingScreen>
      </AppContainer>
    )
  }

  if (needsSetup) {
    return (
      <AppContainer>
        <SetupWizard />
      </AppContainer>
    )
  }

  if (!isAuthenticated) {
    return (
      <AppContainer>
        <Routes>
          <Route path="/setup" element={<SetupWizard />} />
          <Route path="*" element={<SetupWizard />} />
        </Routes>
      </AppContainer>
    )
  }

  return (
    <AppContainer>
      <ErrorBoundary>
        <LCARSLayout>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/boats" element={<BoatList />} />
            <Route path="/boats/new" element={<BoatForm />} />
            <Route path="/boats/:id" element={<BoatDetail />} />
            <Route path="/trips" element={<TripList />} />
            <Route path="/trips/new" element={<TripForm />} />
            <Route path="/trips/:id" element={<TripDetail />} />
            <Route path="/trips/:id/edit" element={<TripEdit />} />
            <Route path="/notes" element={<NotesList />} />
            <Route path="/notes/new" element={<NoteEditor />} />
            <Route path="/notes/:id" element={<NoteDetail />} />
            <Route path="/notes/:id/edit" element={<NoteEditor />} />
            <Route path="/todos" element={<TodoPage />} />
            <Route path="/maintenance" element={<MaintenanceList />} />
            <Route path="/maintenance/templates/new" element={<MaintenanceTemplateForm />} />
            <Route path="/maintenance/templates/:id" element={<MaintenanceTemplateDetail />} />
            <Route path="/maintenance/templates/:id/edit" element={<MaintenanceTemplateForm />} />
            <Route path="/maintenance/events/:id" element={<MaintenanceEventDetail />} />
            <Route path="/map" element={<MapView />} />
            <Route path="/reports" element={<Reports />} />
            <Route path="/reports/license" element={<LicenseProgress />} />
            <Route path="/reports/maintenance" element={<MaintenanceReports />} />
            <Route path="/settings" element={<Settings />} />
            <Route path="/settings/backup" element={<BackupManager />} />
            <Route path="/settings/nautical" element={<NauticalSettings />} />
            <Route path="/calendar" element={<Calendar />} />
            <Route path="/photos" element={<PhotoGallery />} />
            <Route path="/docs" element={<Documentation />} />
            {/* Additional routes will be added in subsequent tasks */}
            <Route path="*" element={<HomePage />} />
          </Routes>
        </LCARSLayout>
      </ErrorBoundary>
    </AppContainer>
  )
}

export default App