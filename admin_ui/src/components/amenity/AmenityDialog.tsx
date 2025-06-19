'use client';

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { UnitAmenityManager } from "./UnitAmenityManager";
import { useState } from "react";

interface AmenityDialogProps {
  unitId: number;
  accommodationId: number;
  trigger?: React.ReactNode;
  buttonLabel?: string;
}

export function AmenityDialog({
  unitId,
  accommodationId,
  trigger,
  buttonLabel = "Manage Amenities"
}: AmenityDialogProps) {
  const [open, setOpen] = useState(false);

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {
          trigger
          ||
          <Button variant="outline">
            {buttonLabel}
          </Button>
        }
      </DialogTrigger>
      <DialogContent className="">
        <DialogHeader>
          <DialogTitle>Unit Amenities</DialogTitle>
          <DialogDescription>
            Select the amenities available in this accommodation unit
          </DialogDescription>
        </DialogHeader>

        <UnitAmenityManager
          unitId={unitId}
          accommodationId={accommodationId}
          onClose={() => setOpen(false)}
        />
      </DialogContent>
    </Dialog>
  );
}
