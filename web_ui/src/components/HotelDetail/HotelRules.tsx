"use client";

import { forwardRef } from 'react';

// Interface for time range
interface TimeRange {
    from: string;
    to: string;
}

// Interface for individual rule section
interface RuleSection {
    title: string;
    content: string | TimeRange | React.ReactNode;
    type: 'text' | 'time' | 'custom';
    fullWidth?: boolean;
}

// Props interface
interface HotelRulesProps {
    title?: string;
    rules?: RuleSection[];
}

// Default rules data
const defaultRules: RuleSection[] = [
    {
        title: "Nhận phòng",
        type: "time",
        content: { from: "14:00", to: "00:00" }
    },
    {
        title: "Trả phòng",
        type: "time",
        content: { from: "07:00", to: "12:00" }
    },
    {
        title: "Hủy/Thanh toán trước",
        type: "text",
        content: "Các chính sách hủy và thanh toán trước có khác biệt dựa trên loại chỗ nghỉ. Vui lòng nhập ngày lưu trú và kiểm tra các điều kiện của phòng mà bạn đã chọn."
    },
    {
        title: "Trẻ em và giường",
        type: "custom",
        content: (
            <>
                <p className="text-gray-700 mb-2">Chính sách trẻ em:</p>
                <p className="text-gray-700">Phù hợp cho tất cả trẻ em.</p>
                <p className="text-gray-700 mt-2">Trẻ em từ 0 đến 5 tuổi được ở miễn phí khi sử dụng giường sẵn có.</p>
            </>
        )
    },
    {
        title: "Thú cưng",
        type: "text",
        content: "Không cho phép mang theo vật nuôi.",
        fullWidth: true
    }
];

const HotelRules = forwardRef<HTMLDivElement, HotelRulesProps>(
    ({
        title = "Quy tắc chung",
        rules = defaultRules
    }, ref) => {
        const renderRuleContent = (rule: RuleSection) => {
            switch (rule.type) {
                case 'time':
                    const timeContent = rule.content as TimeRange;
                    return (
                        <>
                            <div className="flex justify-between mb-2">
                                <span>Từ</span>
                                <span className="font-medium">{timeContent.from}</span>
                            </div>
                            <div className="flex justify-between">
                                <span>Đến</span>
                                <span className="font-medium">{timeContent.to}</span>
                            </div>
                        </>
                    );
                case 'text':
                    return <p className="text-gray-700">{rule.content as string}</p>;
                case 'custom':
                    return rule.content as React.ReactNode;
                default:
                    return null;
            }
        };

        return (
            <div ref={ref} className="mt-12">
                <h2 className="text-2xl font-bold mb-6">{title}</h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                    {rules.map((rule, index) => (
                        <div
                            key={index}
                            className={`border-b pb-6 ${rule.fullWidth ? 'col-span-full' : ''}`}
                        >
                            <h3 className="font-bold mb-4">{rule.title}</h3>
                            {renderRuleContent(rule)}
                        </div>
                    ))}
                </div>
            </div>
        );
    }
);

HotelRules.displayName = 'HotelRules';

export default HotelRules;
