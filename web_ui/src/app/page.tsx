"use client";

import Header from '@/components/Header';
import SearchBar from '@/components/SearchBar';
import { PromotionsSection, DestinationsSection, PropertyTypesSection } from '@/components/Sections';

export default function Home() {
  return (
    <div className="min-h-screen bg-white">
      <Header />
      <SearchBar />
      <PromotionsSection />
      <DestinationsSection />
      <PropertyTypesSection />
    </div>
  );
}