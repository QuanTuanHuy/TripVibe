"use client";

import { useMemo } from "react";
import { Bar } from "react-chartjs-2";
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend
} from "chart.js";

// Register ChartJS components
ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend
);

interface RatingChartProps {
    data: Record<string, number>;
}

export function RatingChart({ data }: RatingChartProps) {
    // Convert and sort data for the chart
    const chartData = useMemo(() => {
        // Get all ratings from 1-10
        const ratings = Array.from({ length: 10 }, (_, i) => (i + 1).toString());

        // Create dataset with sorted ratings
        return {
            labels: ratings.reverse(), // 10 to 1
            datasets: [
                {
                    label: "Number of Ratings",
                    data: ratings.map(rating => data[rating] || 0),
                    backgroundColor: [
                        // Gradient colors from green to yellow to red
                        "rgba(34, 197, 94, 0.8)", // 10 - Green
                        "rgba(74, 222, 128, 0.8)", // 9
                        "rgba(134, 239, 172, 0.8)", // 8
                        "rgba(187, 247, 208, 0.8)", // 7
                        "rgba(250, 240, 137, 0.8)", // 6
                        "rgba(254, 240, 138, 0.8)", // 5
                        "rgba(253, 224, 71, 0.8)", // 4
                        "rgba(254, 202, 202, 0.8)", // 3
                        "rgba(252, 165, 165, 0.8)", // 2
                        "rgba(248, 113, 113, 0.8)", // 1 - Red
                    ],
                    borderWidth: 1,
                    borderColor: "rgba(255, 255, 255, 0.2)",
                },
            ],
        };
    }, [data]);

    const options = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                display: false,
            },
            tooltip: {
                callbacks: {
                    label: (context: any) => {
                        const value = context.raw;
                        return `${value} rating${value !== 1 ? "s" : ""}`;
                    },
                },
            },
        },
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    precision: 0,
                },
            },
            x: {
                title: {
                    display: true,
                    text: "Rating Value",
                    color: "rgba(100, 116, 139, 0.7)",
                    padding: { top: 10 },
                    font: {
                        size: 12,
                    },
                },
            },
        },
    };

    return (
        <div className="h-[300px]">
            <Bar data={chartData} options={options as any} />
        </div>
    );
}