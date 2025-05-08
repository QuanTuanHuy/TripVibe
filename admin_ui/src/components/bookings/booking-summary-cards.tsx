"use client";

import { Card, CardContent } from "@/components/ui/card";
import { 
  CalendarClock, 
  Calendar, 
  CheckSquare, 
  Clock, 
  Users, 
  LogIn, 
  LogOut
} from "lucide-react";

interface BookingSummary {
  total: number;
  pending: number;
  confirmed: number;
  checkedIn: number;
  today: {
    checkIn: number;
    checkOut: number;
  };
}

interface BookingSummaryCardsProps {
  summary: BookingSummary;
}

export function BookingSummaryCards({ summary }: BookingSummaryCardsProps) {
  return (
    <>
      <Card>
        <CardContent className="p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-muted-foreground">Tổng đặt phòng</p>
              <p className="text-3xl font-bold">{summary.total}</p>
            </div>
            <div className="rounded-full p-2 bg-primary/10">
              <Calendar className="h-6 w-6 text-primary" />
            </div>
          </div>
        </CardContent>
      </Card>
      
      <Card>
        <CardContent className="p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-muted-foreground">Chờ xác nhận</p>
              <p className="text-3xl font-bold">{summary.pending}</p>
            </div>
            <div className="rounded-full p-2 bg-amber-100">
              <CalendarClock className="h-6 w-6 text-amber-600" />
            </div>
          </div>
        </CardContent>
      </Card>
      
      <Card>
        <CardContent className="p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-muted-foreground">Đã xác nhận</p>
              <p className="text-3xl font-bold">{summary.confirmed}</p>
            </div>
            <div className="rounded-full p-2 bg-green-100">
              <CheckSquare className="h-6 w-6 text-green-600" />
            </div>
          </div>
        </CardContent>
      </Card>
      
      <Card>
        <CardContent className="p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-muted-foreground">Đang lưu trú</p>
              <p className="text-3xl font-bold">{summary.checkedIn}</p>
            </div>
            <div className="rounded-full p-2 bg-blue-100">
              <Users className="h-6 w-6 text-blue-600" />
            </div>
          </div>
        </CardContent>
      </Card>
      
      <Card>
        <CardContent className="p-6 flex flex-col space-y-3">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-muted-foreground">Hôm nay</p>
            </div>
            <div className="rounded-full p-2 bg-purple-100">
              <Clock className="h-6 w-6 text-purple-600" />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-2 pt-2">
            <div className="flex items-center">
              <LogIn className="h-4 w-4 mr-2 text-green-600" />
              <div>
                <p className="text-xs text-muted-foreground">Check-in</p>
                <p className="text-lg font-semibold">{summary.today.checkIn}</p>
              </div>
            </div>
            <div className="flex items-center">
              <LogOut className="h-4 w-4 mr-2 text-blue-600" />
              <div>
                <p className="text-xs text-muted-foreground">Check-out</p>
                <p className="text-lg font-semibold">{summary.today.checkOut}</p>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </>
  );
}