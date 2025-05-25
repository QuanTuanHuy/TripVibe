'use client';

import { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { AmenityDialog } from '@/components/amenity';
import { accommodationService, amenityService } from '@/services';
import { Amenity, AmenityGroup } from '@/types/accommodation';
import { Loader2 } from 'lucide-react';

interface RoomAmenitiesProps {
  roomId: number;
  accommodationId: number;
}

export function RoomAmenities({ roomId, accommodationId }: RoomAmenitiesProps) {
  const [loading, setLoading] = useState(true);
  const [amenities, setAmenities] = useState<Amenity[]>([]);
  const [amenityGroups, setAmenityGroups] = useState<AmenityGroup[]>([]);
  const [unitAmenityIds, setUnitAmenityIds] = useState<number[]>([]);

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);

        // Load amenity groups with type UNIT
        const amenityGroupsResponse = await accommodationService.getAmenityGroups({ type: 'UNIT' });
        setAmenityGroups(amenityGroupsResponse.data || []);

        // Flatten all amenities from all groups
        const allAmenities = amenityGroupsResponse.data?.flatMap(group => group.amenities) || [];
        setAmenities(allAmenities);

        // Load currently selected amenities for this unit
        const unitAmenities = await amenityService.getUnitAmenities(accommodationId, roomId);
        setUnitAmenityIds(unitAmenities);
      } catch (error) {
        console.error('Error loading amenities data:', error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [roomId, accommodationId]);

  // Create a map of amenity groups with their amenities filtered to only those selected for this unit
  const selectedAmenitiesByGroup = amenityGroups.map(group => ({
    ...group,
    amenities: group.amenities.filter(amenity => unitAmenityIds.includes(amenity.id))
  })).filter(group => group.amenities.length > 0);

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <div>
          <CardTitle>Room Amenities</CardTitle>
          <CardDescription>
            Manage amenities available in this room
          </CardDescription>
        </div>
        <AmenityDialog
          unitId={roomId}
          accommodationId={accommodationId}
          trigger={<Button>Manage Amenities</Button>}
        />
      </CardHeader>
      <CardContent>
        {loading ? (
          <div className="flex justify-center p-8">
            <Loader2 className="h-8 w-8 animate-spin" />
          </div>
        ) : selectedAmenitiesByGroup.length === 0 ? (
          <div className="text-center py-8 text-muted-foreground">
            <p>No amenities have been added to this room yet.</p>
            <p className="mt-2">Click "Manage Amenities" to add amenities to this room.</p>
          </div>
        ) : (
          <div className="space-y-6">
            {selectedAmenitiesByGroup.map(group => (
              <div key={group.id} className="border rounded-md p-4">
                <h3 className="font-medium mb-2 flex items-center gap-2">
                  {group.icon && <span>{group.icon}</span>}
                  {group.name}
                </h3>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-2">
                  {group.amenities.map(amenity => (
                    <div key={amenity.id} className="flex items-center gap-2 text-sm">
                      {amenity.icon && <span>{amenity.icon}</span>}
                      <span>{amenity.name}</span>
                      {amenity.isPaid && <span className="text-xs text-muted-foreground">(paid)</span>}
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
}
