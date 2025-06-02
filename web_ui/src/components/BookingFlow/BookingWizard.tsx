"use client";

import React from 'react';
import { Check, ArrowLeft } from 'lucide-react';
import { useBooking } from '@/context/BookingContext';

interface StepInfo {
    key: string;
    title: string;
    description: string;
}

const steps: StepInfo[] = [
    {
        key: 'room-selection',
        title: 'Chọn phòng',
        description: 'Chọn loại phòng và số lượng'
    },
    {
        key: 'guest-info',
        title: 'Thông tin khách',
        description: 'Điền thông tin liên hệ'
    },
    {
        key: 'review',
        title: 'Xem lại',
        description: 'Kiểm tra thông tin đặt phòng'
    },
    {
        key: 'payment',
        title: 'Thanh toán',
        description: 'Chọn phương thức thanh toán'
    },
    {
        key: 'confirmation',
        title: 'Xác nhận',
        description: 'Hoàn tất đặt phòng'
    }
];

interface BookingWizardProps {
    children: React.ReactNode;
}

export default function BookingWizard({ children }: BookingWizardProps) {
    const { state, goToStep, hasSelectedRooms, dispatch } = useBooking();

    const getCurrentStepIndex = () => {
        return steps.findIndex(step => step.key === state.currentStep);
    };

    const isStepCompleted = (stepKey: string) => {
        const currentIndex = getCurrentStepIndex();
        const stepIndex = steps.findIndex(step => step.key === stepKey);
        return stepIndex < currentIndex;
    };

    const isStepActive = (stepKey: string) => {
        return stepKey === state.currentStep;
    };

    const canNavigateToStep = (stepKey: string) => {
        const stepIndex = steps.findIndex(step => step.key === stepKey);
        const currentIndex = getCurrentStepIndex();

        // Can always go back to previous steps
        if (stepIndex < currentIndex) return true;
        // Check if requirements are met for each step
        switch (stepKey) {
            case 'room-selection':
                return true;
            case 'guest-info':
                return hasSelectedRooms();
            case 'review':
                return hasSelectedRooms() && !!state.guestInfo;
            case 'payment':
                return hasSelectedRooms() && !!state.guestInfo;
            case 'confirmation':
                return state.paymentStatus === 'success';
            default:
                return false;
        }
    };

    const handleStepClick = (stepKey: string) => {
        if (canNavigateToStep(stepKey)) {
            goToStep(stepKey as any);
        }
    };

    const handleBack = () => {
        const currentIndex = getCurrentStepIndex();
        if (currentIndex > 0) {
            goToStep(steps[currentIndex - 1].key as any);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header with Steps */}
            <div className="bg-white shadow-sm border-b border-gray-200">
                <div className="max-w-7xl mx-auto px-4 py-6">
                    {/* Back Button */}
                    {state.currentStep !== 'room-selection' && (
                        <button
                            onClick={handleBack}
                            className="flex items-center gap-2 text-blue-600 hover:text-blue-700 mb-6 group"
                        >
                            <ArrowLeft size={16} className="group-hover:-translate-x-1 transition-transform" />
                            <span>Quay lại</span>
                        </button>
                    )}

                    {/* Steps Navigation */}
                    <div className="flex items-center justify-center">
                        {steps.map((step, index) => (
                            <React.Fragment key={step.key}>
                                <div
                                    className={`flex items-center cursor-pointer group ${canNavigateToStep(step.key) ? 'hover:scale-105' : 'cursor-not-allowed'
                                        } transition-transform`}
                                    onClick={() => handleStepClick(step.key)}
                                >
                                    {/* Step Circle */}
                                    <div className={`
                                        flex items-center justify-center w-10 h-10 rounded-full border-2 font-semibold text-sm
                                        ${isStepCompleted(step.key)
                                            ? 'bg-green-500 border-green-500 text-white'
                                            : isStepActive(step.key)
                                                ? 'bg-blue-600 border-blue-600 text-white'
                                                : canNavigateToStep(step.key)
                                                    ? 'border-gray-300 text-gray-600 group-hover:border-blue-600 group-hover:text-blue-600'
                                                    : 'border-gray-200 text-gray-400'
                                        }
                                    `}>
                                        {isStepCompleted(step.key) ? (
                                            <Check size={16} />
                                        ) : (
                                            <span>{index + 1}</span>
                                        )}
                                    </div>

                                    {/* Step Info */}
                                    <div className="ml-3 min-w-0 hidden md:block">
                                        <div className={`text-sm font-medium ${isStepActive(step.key)
                                                ? 'text-blue-600'
                                                : isStepCompleted(step.key)
                                                    ? 'text-green-600'
                                                    : canNavigateToStep(step.key)
                                                        ? 'text-gray-900 group-hover:text-blue-600'
                                                        : 'text-gray-400'
                                            }`}>
                                            {step.title}
                                        </div>
                                        <div className="text-xs text-gray-500">
                                            {step.description}
                                        </div>
                                    </div>
                                </div>

                                {/* Connector Line */}
                                {index < steps.length - 1 && (
                                    <div className="flex-1 mx-4 h-px bg-gray-200 min-w-[50px] hidden sm:block">
                                        <div className={`h-full transition-all duration-300 ${isStepCompleted(steps[index + 1].key) ? 'bg-green-500' : 'bg-gray-200'
                                            }`} />
                                    </div>
                                )}
                            </React.Fragment>
                        ))}
                    </div>

                    {/* Mobile Step Info */}
                    <div className="mt-4 text-center md:hidden">
                        <div className="text-lg font-semibold text-gray-900">
                            {steps[getCurrentStepIndex()]?.title}
                        </div>
                        <div className="text-sm text-gray-600">
                            {steps[getCurrentStepIndex()]?.description}
                        </div>
                    </div>
                </div>
            </div>

            {/* Main Content */}
            <div className="max-w-7xl mx-auto px-4 py-8">
                {children}
            </div>

            {/* Loading Overlay */}
            {state.isLoading && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg p-6 max-w-sm w-full mx-4">
                        <div className="flex items-center gap-3">
                            <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
                            <span className="text-gray-700">Đang xử lý...</span>
                        </div>
                    </div>
                </div>
            )}

            {/* Error Toast */}
            {state.error && (
                <div className="fixed bottom-4 right-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded shadow-lg z-50">
                    <div className="flex items-center gap-2">
                        <span>❌</span>
                        <span>{state.error}</span>                        <button
                            onClick={() => dispatch({ type: 'SET_ERROR', payload: null })}
                            className="ml-2 text-red-700 hover:text-red-900"
                        >
                            ✕
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
