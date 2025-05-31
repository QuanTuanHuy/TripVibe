'use client';

import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { useRouter } from 'next/navigation';
import { List, CheckSquare } from 'lucide-react';

interface AmenityQuickLinkProps {
  hotelId: number;
  roomId: number;
  amenityCount?: number;
}

export function AmenityQuickLink({ hotelId, roomId, amenityCount = 0 }: AmenityQuickLinkProps) {
  const router = useRouter();

  const handleManageAmenities = () => {
    router.push(`/hotels/manage/${hotelId}/rooms/${roomId}/amenities`);
  };

  return (
    <Card className="overflow-hidden">
      <CardHeader className="bg-primary/5">
        <CardTitle className="flex items-center gap-2">
          <CheckSquare className="h-5 w-5" />
          Room Amenities
        </CardTitle>
        <CardDescription>
          Manage features and amenities available in this room
        </CardDescription>
      </CardHeader>
      <CardContent className="pt-6">
        <div className="flex items-center justify-between">
          <div className="space-y-1">
            <p className="text-2xl font-bold">{amenityCount}</p>
            <p className="text-sm text-muted-foreground">Amenities configured</p>
          </div>
          <div className="flex items-center text-muted-foreground">
            <List className="h-8 w-8" />
          </div>
        </div>
      </CardContent>
      <CardFooter className="bg-gray-50 p-2">
        <Button
          onClick={handleManageAmenities}
          className="w-full"
          variant="ghost"
        >
          Manage Amenities
        </Button>
      </CardFooter>
    </Card>
  );
}
