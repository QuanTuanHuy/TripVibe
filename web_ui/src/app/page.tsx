"use client";

import Header from '@/components/Header';
import SearchForm from '@/components/SearchForm';
import { PromotionsSection, DestinationsSection, PropertyTypesSection } from '@/components/Sections';

export default function Home() {
  return (
    <div className="min-h-screen bg-white">
      <Header />
      <SearchForm />
      <PromotionsSection />
      <DestinationsSection />
      <PropertyTypesSection />
    </div>
  );
}