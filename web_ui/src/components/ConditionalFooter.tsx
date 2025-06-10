'use client';

import { usePathname } from 'next/navigation';
import Footer from '@/components/Footer';

const ConditionalFooter: React.FC = () => {
    const pathname = usePathname();

    const routesWithoutFooter = ['/inbox'];

    const shouldHideFooter = routesWithoutFooter.some(route =>
        pathname.startsWith(route)
    );

    if (shouldHideFooter) {
        return null;
    }

    return <Footer />;
};

export default ConditionalFooter;
