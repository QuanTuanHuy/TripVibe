'use client';

import React, { createContext, useContext, useState, useCallback } from 'react';
import { SidebarState, SidebarTab } from '@/types/chat/sidebar';

interface SidebarContextValue {
    sidebarState: SidebarState;
    openSidebar: (tab?: SidebarTab) => void;
    closeSidebar: () => void;
    toggleSidebar: (tab?: SidebarTab) => void;
    setActiveTab: (tab: SidebarTab) => void;
    isOpen: boolean;
    activeTab: SidebarTab;
}

const SidebarContext = createContext<SidebarContextValue | undefined>(undefined);

interface SidebarProviderProps {
    children: React.ReactNode;
    initialState?: Partial<SidebarState>;
}

export const SidebarProvider: React.FC<SidebarProviderProps> = ({
    children,
    initialState = {}
}) => {
    const [sidebarState, setSidebarState] = useState<SidebarState>({
        isOpen: false,
        activeTab: 'info',
        ...initialState
    });

    const openSidebar = useCallback((tab: SidebarTab = 'info') => {
        setSidebarState({
            isOpen: true,
            activeTab: tab
        });
    }, []);

    const closeSidebar = useCallback(() => {
        setSidebarState(prev => ({
            ...prev,
            isOpen: false
        }));
    }, []);

    const toggleSidebar = useCallback((tab: SidebarTab = 'info') => {
        setSidebarState(prev => ({
            isOpen: !prev.isOpen,
            activeTab: prev.isOpen ? prev.activeTab : tab
        }));
    }, []);

    const setActiveTab = useCallback((tab: SidebarTab) => {
        setSidebarState(prev => ({
            ...prev,
            activeTab: tab
        }));
    }, []);

    const value: SidebarContextValue = {
        sidebarState,
        openSidebar,
        closeSidebar,
        toggleSidebar,
        setActiveTab,
        isOpen: sidebarState.isOpen,
        activeTab: sidebarState.activeTab
    };

    return (
        <SidebarContext.Provider value={value}>
            {children}
        </SidebarContext.Provider>
    );
};

export const useSidebar = (): SidebarContextValue => {
    const context = useContext(SidebarContext);
    if (context === undefined) {
        throw new Error('useSidebar must be used within a SidebarProvider');
    }
    return context;
};

export default SidebarContext;
