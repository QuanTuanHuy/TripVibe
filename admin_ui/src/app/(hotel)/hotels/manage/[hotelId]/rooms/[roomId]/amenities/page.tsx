'use client';

import { UnitAmenityManager } from '@/components/amenity';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { ArrowLeft } from 'lucide-react';
import { useParams, useRouter } from 'next/navigation';
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, BreadcrumbList, BreadcrumbPage, BreadcrumbSeparator } from '@/components/ui/breadcrumb';

export default function UnitAmenitiesPage() {
  const params = useParams();
  const router = useRouter();
  const hotelId = params.hotelId as string;
  const roomId = params.roomId as string;

  const handleGoBack = () => {
    router.back();
  };

  return (
    <div className="container mx-auto py-6 space-y-6">
      <div className="flex items-center justify-between">
        <Breadcrumb>
          <BreadcrumbList>
            <BreadcrumbItem>
              <BreadcrumbLink href="/hotels/manage">Hotels</BreadcrumbLink>
            </BreadcrumbItem>
            <BreadcrumbSeparator />
            <BreadcrumbItem>
              <BreadcrumbLink href={`/hotels/manage/${hotelId}`}>Hotel Details</BreadcrumbLink>
            </BreadcrumbItem>
            <BreadcrumbSeparator />
            <BreadcrumbItem>
              <BreadcrumbLink href={`/hotels/manage/${hotelId}/rooms/${roomId}`}>Room Details</BreadcrumbLink>
            </BreadcrumbItem>
            <BreadcrumbSeparator />
            <BreadcrumbPage>Amenities</BreadcrumbPage>
          </BreadcrumbList>
        </Breadcrumb>

        <Button variant="outline" onClick={handleGoBack}>
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back
        </Button>
      </div>

      <Card className="p-6">
        <UnitAmenityManager 
          unitId={parseInt(roomId)} 
          accommodationId={parseInt(hotelId)}
          variant="card"
        />
      </Card>
    </div>
  );
}
