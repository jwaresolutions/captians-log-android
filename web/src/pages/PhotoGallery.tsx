import React, { useState, useEffect, useRef } from 'react'
import styled from 'styled-components'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { LCARSDataDisplay } from '../components/lcars/LCARSDataDisplay'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'
import { useTrips } from '../hooks/useTrips'
import { apiService } from '../services/api'
import { Photo } from '../types/api'

const GalleryContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.lg};
  max-width: 1400px;
  margin: 0 auto;
`

const FilterControls = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.md};
  align-items: center;
  margin-bottom: ${props => props.theme.spacing.md};
  flex-wrap: wrap;
`

const PhotoGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: ${props => props.theme.spacing.md};
  margin-top: ${props => props.theme.spacing.md};
`

const PhotoCard = styled.div`
  background: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  border-radius: ${props => props.theme.borderRadius.sm};
  overflow: hidden;
  cursor: pointer;
  transition: all ${props => props.theme.animation.normal} ease;

  &:hover {
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    transform: translateY(-2px);
    box-shadow: ${props => props.theme.shadows.glow};
  }
`

const PhotoImage = styled.img`
  width: 100%;
  height: 200px;
  object-fit: cover;
  display: block;
`

const PhotoInfo = styled.div`
  padding: ${props => props.theme.spacing.sm};
`

const PhotoTitle = styled.div`
  color: ${props => props.theme.colors.text.primary};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  font-size: ${props => props.theme.typography.fontSize.sm};
  margin-bottom: ${props => props.theme.spacing.xs};
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
`

const PhotoMeta = styled.div`
  color: ${props => props.theme.colors.text.secondary};
  font-size: ${props => props.theme.typography.fontSize.xs};
  display: flex;
  justify-content: space-between;
  align-items: center;
`

const PhotoType = styled.span<{ $type: 'trip' }>`
  background: ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.text.primary};
  padding: 2px 6px;
  border-radius: 2px;
  font-size: 10px;
  text-transform: uppercase;
  font-weight: bold;
`

const LightboxOverlay = styled.div<{ $isOpen: boolean }>`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  display: ${props => props.$isOpen ? 'flex' : 'none'};
  align-items: center;
  justify-content: center;
  z-index: ${props => props.theme.zIndex.modal};
  padding: ${props => props.theme.spacing.lg};
`

const LightboxContent = styled.div`
  max-width: 90vw;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: ${props => props.theme.spacing.md};
`

const LightboxImage = styled.img`
  max-width: 100%;
  max-height: 80vh;
  object-fit: contain;
  border: 2px solid ${props => props.theme.colors.primary.neonCarrot};
`

const LightboxInfo = styled.div`
  background: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  padding: ${props => props.theme.spacing.md};
  border-radius: ${props => props.theme.borderRadius.sm};
  color: ${props => props.theme.colors.text.primary};
  text-align: center;
  max-width: 500px;
`

const LightboxControls = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.md};
`

const EmptyState = styled.div`
  text-align: center;
  padding: ${props => props.theme.spacing.xl};
  color: ${props => props.theme.colors.text.secondary};
  font-style: italic;
`

const UploadModal = styled.div<{ $isOpen: boolean }>`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.85);
  display: ${props => props.$isOpen ? 'flex' : 'none'};
  align-items: center;
  justify-content: center;
  z-index: ${props => props.theme.zIndex.modal};
  padding: ${props => props.theme.spacing.lg};
`

const UploadForm = styled.div`
  background: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.neonCarrot};
  border-radius: ${props => props.theme.borderRadius.md};
  padding: ${props => props.theme.spacing.lg};
  max-width: 500px;
  width: 100%;
`

const FormGroup = styled.div`
  margin-bottom: ${props => props.theme.spacing.md};

  label {
    display: block;
    color: ${props => props.theme.colors.primary.anakiwa};
    font-weight: bold;
    text-transform: uppercase;
    font-size: ${props => props.theme.typography.fontSize.sm};
    margin-bottom: ${props => props.theme.spacing.xs};
  }

  select, input[type="file"] {
    width: 100%;
    padding: ${props => props.theme.spacing.sm};
    background: ${props => props.theme.colors.surface.medium};
    border: 1px solid ${props => props.theme.colors.primary.anakiwa};
    border-radius: ${props => props.theme.borderRadius.sm};
    color: ${props => props.theme.colors.text.primary};
    font-family: ${props => props.theme.typography.fontFamily.primary};
  }
`

const UploadPreview = styled.div`
  margin-top: ${props => props.theme.spacing.md};
  text-align: center;

  img {
    max-width: 100%;
    max-height: 200px;
    border: 2px solid ${props => props.theme.colors.primary.anakiwa};
    border-radius: ${props => props.theme.borderRadius.sm};
  }
`

const ButtonRow = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.md};
  justify-content: flex-end;
  margin-top: ${props => props.theme.spacing.lg};
`

const StatsPanel = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${props => props.theme.spacing.md};
  margin-bottom: ${props => props.theme.spacing.lg};
`

interface PhotoWithContext extends Photo {
  contextType: 'trip'
  contextTitle: string
  contextDate: string
}

type FilterType = 'all' | 'trips'

export const PhotoGallery: React.FC = () => {
  const [photos, setPhotos] = useState<PhotoWithContext[]>([])
  const [filteredPhotos, setFilteredPhotos] = useState<PhotoWithContext[]>([])
  const [filter, setFilter] = useState<FilterType>('all')
  const [selectedPhoto, setSelectedPhoto] = useState<PhotoWithContext | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  // Upload state
  const [isUploadOpen, setIsUploadOpen] = useState(false)
  const [uploadFile, setUploadFile] = useState<File | null>(null)
  const [uploadPreview, setUploadPreview] = useState<string | null>(null)
  const [uploadEntityType, setUploadEntityType] = useState<string>('trip')
  const [uploadEntityId, setUploadEntityId] = useState<string>('')
  const [isUploading, setIsUploading] = useState(false)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const { data: trips, isLoading: tripsLoading, refetch: refetchTrips } = useTrips()

  useEffect(() => {
    const allPhotos: PhotoWithContext[] = []
    
    // Add photos from trips
    if (trips) {
      trips.forEach(trip => {
        if (trip.photos) {
          trip.photos.forEach(photo => {
            allPhotos.push({
              ...photo,
              contextType: 'trip',
              contextTitle: `Trip: ${trip.boat?.name || 'Unknown Boat'}`,
              contextDate: new Date(trip.startTime).toLocaleDateString(),
            })
          })
        }
      })
    }
    
    // Sort photos by creation date (newest first)
    allPhotos.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    
    setPhotos(allPhotos)
    setIsLoading(tripsLoading)
  }, [trips, tripsLoading])

  useEffect(() => {
    let filtered = photos
    
    if (filter === 'trips') {
      filtered = photos.filter(photo => photo.contextType === 'trip')
    }
    
    setFilteredPhotos(filtered)
  }, [photos, filter])

  const openLightbox = (photo: PhotoWithContext) => {
    setSelectedPhoto(photo)
  }

  const closeLightbox = () => {
    setSelectedPhoto(null)
  }

  const navigatePhoto = (direction: 'prev' | 'next') => {
    if (!selectedPhoto) return
    
    const currentIndex = filteredPhotos.findIndex(p => p.id === selectedPhoto.id)
    let newIndex = currentIndex
    
    if (direction === 'prev') {
      newIndex = currentIndex > 0 ? currentIndex - 1 : filteredPhotos.length - 1
    } else {
      newIndex = currentIndex < filteredPhotos.length - 1 ? currentIndex + 1 : 0
    }
    
    setSelectedPhoto(filteredPhotos[newIndex])
  }

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes'
    
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  const tripPhotos = photos.filter(p => p.contextType === 'trip')

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      setUploadFile(file)
      // Create preview
      const reader = new FileReader()
      reader.onload = (e) => {
        setUploadPreview(e.target?.result as string)
      }
      reader.readAsDataURL(file)
    }
  }

  const handleUpload = async () => {
    if (!uploadFile || !uploadEntityId) {
      alert('Please select a file and choose what to attach it to')
      return
    }

    setIsUploading(true)
    try {
      await apiService.uploadPhoto(uploadFile, uploadEntityType, uploadEntityId)
      // Reset and close
      setUploadFile(null)
      setUploadPreview(null)
      setUploadEntityId('')
      setIsUploadOpen(false)
      // Refresh photos
      refetchTrips()
      alert('Photo uploaded successfully!')
    } catch (error) {
      console.error('Failed to upload photo:', error)
      alert('Failed to upload photo. Please try again.')
    } finally {
      setIsUploading(false)
    }
  }

  const closeUploadModal = () => {
    setIsUploadOpen(false)
    setUploadFile(null)
    setUploadPreview(null)
    setUploadEntityId('')
  }

  return (
    <GalleryContainer>
      <LCARSHeader>Photo Gallery</LCARSHeader>
      
      {/* Photo Statistics */}
      <StatsPanel>
        <LCARSDataDisplay
          label="Total Photos"
          value={photos.length.toString()}
          valueColor="neonCarrot"
        />
        <LCARSDataDisplay
          label="Trip Photos"
          value={tripPhotos.length.toString()}
          valueColor="anakiwa"
        />
        <LCARSDataDisplay
          label="Maintenance Photos"
          value="0"
          valueColor="lilac"
        />
        <LCARSDataDisplay
          label="Total Size"
          value={formatFileSize(photos.reduce((sum, photo) => sum + (photo.sizeBytes || 0), 0))}
          valueColor="anakiwa"
        />
      </StatsPanel>

      <LCARSPanel title="Photo Collection">
        {/* Filter Controls */}
        <FilterControls>
          <ReadOnlyGuard>
            <LCARSButton
              onClick={() => setIsUploadOpen(true)}
              variant="accent"
              size="sm"
            >
              Upload Photo
            </LCARSButton>
          </ReadOnlyGuard>
          <LCARSButton
            onClick={() => setFilter('all')}
            variant={filter === 'all' ? 'primary' : 'secondary'}
            size="sm"
          >
            All Photos ({photos.length})
          </LCARSButton>
          <LCARSButton
            onClick={() => setFilter('trips')}
            variant={filter === 'trips' ? 'primary' : 'secondary'}
            size="sm"
          >
            Trip Photos ({tripPhotos.length})
          </LCARSButton>
          <LCARSButton
            onClick={() => setFilter('trips')}
            variant={filter === 'trips' ? 'primary' : 'secondary'}
            size="sm"
            disabled
          >
            Maintenance Photos (Coming Soon)
          </LCARSButton>
        </FilterControls>

        {/* Photo Grid */}
        {isLoading ? (
          <div style={{ textAlign: 'center', padding: '40px' }}>
            <div style={{ color: '#6688CC' }}>Loading photos...</div>
          </div>
        ) : filteredPhotos.length === 0 ? (
          <EmptyState>
            No photos found. Photos will appear here when you attach them to trips.
          </EmptyState>
        ) : (
          <PhotoGrid>
            {filteredPhotos.map((photo) => (
              <PhotoCard key={photo.id} onClick={() => openLightbox(photo)}>
                <PhotoImage
                  src={photo.webOptimizedPath || photo.originalPath}
                  alt={photo.contextTitle}
                  loading="lazy"
                />
                <PhotoInfo>
                  <PhotoTitle>{photo.contextTitle}</PhotoTitle>
                  <PhotoMeta>
                    <PhotoType $type={photo.contextType}>
                      {photo.contextType}
                    </PhotoType>
                    <span>{photo.contextDate}</span>
                  </PhotoMeta>
                </PhotoInfo>
              </PhotoCard>
            ))}
          </PhotoGrid>
        )}
      </LCARSPanel>

      {/* Lightbox */}
      <LightboxOverlay $isOpen={!!selectedPhoto} onClick={closeLightbox}>
        {selectedPhoto && (
          <LightboxContent onClick={(e) => e.stopPropagation()}>
            <LightboxImage
              src={selectedPhoto.webOptimizedPath || selectedPhoto.originalPath}
              alt={selectedPhoto.contextTitle}
            />
            <LightboxInfo>
              <div style={{ marginBottom: '10px' }}>
                <strong>{selectedPhoto.contextTitle}</strong>
              </div>
              <div style={{ fontSize: '14px', color: '#CCCCCC' }}>
                <div>Date: {selectedPhoto.contextDate}</div>
                <div>Size: {formatFileSize(selectedPhoto.sizeBytes || 0)}</div>
                <div>Type: {selectedPhoto.mimeType}</div>
                {selectedPhoto.metadata && (
                  <div>
                    Dimensions: {selectedPhoto.metadata.width} × {selectedPhoto.metadata.height}
                  </div>
                )}
              </div>
            </LightboxInfo>
            <LightboxControls>
              <LCARSButton
                onClick={() => navigatePhoto('prev')}
                variant="secondary"
                size="sm"
              >
                ← Previous
              </LCARSButton>
              <LCARSButton
                onClick={closeLightbox}
                size="sm"
              >
                Close
              </LCARSButton>
              <LCARSButton
                onClick={() => navigatePhoto('next')}
                variant="secondary"
                size="sm"
              >
                Next →
              </LCARSButton>
            </LightboxControls>
          </LightboxContent>
        )}
      </LightboxOverlay>

      {/* Upload Modal */}
      <UploadModal $isOpen={isUploadOpen} onClick={closeUploadModal}>
        <UploadForm onClick={(e) => e.stopPropagation()}>
          <LCARSHeader level={3}>Upload Photo</LCARSHeader>

          <FormGroup>
            <label>Select Photo</label>
            <input
              type="file"
              accept="image/*"
              onChange={handleFileSelect}
              ref={fileInputRef}
            />
          </FormGroup>

          {uploadPreview && (
            <UploadPreview>
              <img src={uploadPreview} alt="Preview" />
            </UploadPreview>
          )}

          <FormGroup>
            <label>Attach To</label>
            <select
              value={uploadEntityType}
              onChange={(e) => {
                setUploadEntityType(e.target.value)
                setUploadEntityId('')
              }}
            >
              <option value="trip">Trip</option>
            </select>
          </FormGroup>

          <FormGroup>
            <label>Select {uploadEntityType === 'trip' ? 'Trip' : 'Item'}</label>
            <select
              value={uploadEntityId}
              onChange={(e) => setUploadEntityId(e.target.value)}
            >
              <option value="">-- Select --</option>
              {uploadEntityType === 'trip' && trips?.map(trip => (
                <option key={trip.id} value={trip.id}>
                  {trip.boat?.name || 'Unknown Boat'} - {new Date(trip.startTime).toLocaleDateString()}
                </option>
              ))}
            </select>
          </FormGroup>

          <ButtonRow>
            <LCARSButton
              variant="secondary"
              onClick={closeUploadModal}
              disabled={isUploading}
            >
              Cancel
            </LCARSButton>
            <LCARSButton
              onClick={handleUpload}
              disabled={!uploadFile || !uploadEntityId || isUploading}
            >
              {isUploading ? 'Uploading...' : 'Upload'}
            </LCARSButton>
          </ButtonRow>
        </UploadForm>
      </UploadModal>
    </GalleryContainer>
  )
}