"use client";

import { useState, useEffect, useRef } from "react";
import { useSearchParams } from "next/navigation";
import { Loader2, Send, ChevronLeft } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { ScrollArea } from "@/components/ui/scroll-area";
import { formatDistanceToNow } from "date-fns";
import { vi } from "date-fns/locale";
import { cn } from "@/lib/utils";
import ChatListItem from "@/components/inbox/ChatListItem";
import ChatMessage from "@/components/inbox/ChatMessage";

// Mock user for demo purposes
const MOCK_USER = {
    id: 1,
    name: "Chủ khách sạn",
    email: "owner@example.com",
    role: "OWNER"
};

// Mock data for chat rooms
const MOCK_CHAT_ROOMS = [
    {
        id: 1,
        bookingId: 12345,
        lastMessage: {
            id: 101,
            content: "Xin chào, tôi có một số câu hỏi về phòng đặt",
            createdAt: Date.now() / 1000 - 3600, // 1 hour ago
            senderName: "Nguyễn Văn A",
            type: "USER"
        },
        participants: [
            {
                userId: 1,
                name: "Chủ khách sạn",
                avatar: ""
            },
            {
                userId: 2,
                name: "Nguyễn Văn A",
                avatar: ""
            }
        ],
        unreadCount: 3
    },
    {
        id: 2,
        bookingId: 12346,
        lastMessage: {
            id: 201,
            content: "Cảm ơn bạn đã xác nhận đặt phòng của tôi!",
            createdAt: Date.now() / 1000 - 86400, // 1 day ago
            senderName: "Trần Thị B",
            type: "USER"
        },
        participants: [
            {
                userId: 1,
                name: "Chủ khách sạn",
                avatar: ""
            },
            {
                userId: 3,
                name: "Trần Thị B",
                avatar: ""
            }
        ],
        unreadCount: 0
    },
    {
        id: 3,
        bookingId: 12347,
        lastMessage: {
            id: 301,
            content: "Phòng của tôi có bao gồm bữa sáng không?",
            createdAt: Date.now() / 1000 - 172800, // 2 days ago
            senderName: "Lê Văn C",
            type: "USER"
        },
        participants: [
            {
                userId: 1,
                name: "Chủ khách sạn",
                avatar: ""
            },
            {
                userId: 4,
                name: "Lê Văn C",
                avatar: ""
            }
        ],
        unreadCount: 1
    },
    {
        id: 4,
        bookingId: 12348,
        lastMessage: {
            id: 401,
            content: "Tôi sẽ đến muộn một chút, khoảng 10 giờ tối",
            createdAt: Date.now() / 1000 - 259200, // 3 days ago
            senderName: "Phạm Thị D",
            type: "USER"
        },
        participants: [
            {
                userId: 1,
                name: "Chủ khách sạn",
                avatar: ""
            },
            {
                userId: 5,
                name: "Phạm Thị D",
                avatar: ""
            }
        ],
        unreadCount: 0
    },
    {
        id: 5,
        bookingId: 12349,
        lastMessage: {
            id: 501,
            content: "Khách sạn có bãi đậu xe không?",
            createdAt: Date.now() / 1000 - 345600, // 4 days ago
            senderName: "Hoàng Văn E",
            type: "USER"
        },
        participants: [
            {
                userId: 1,
                name: "Chủ khách sạn",
                avatar: ""
            },
            {
                userId: 6,
                name: "Hoàng Văn E",
                avatar: ""
            }
        ],
        unreadCount: 2
    }
];

// Mock messages for chat room 1
const MOCK_MESSAGES_ROOM_1 = [
    {
        id: 1001,
        chatRoomId: 1,
        senderId: null,
        senderName: "Hệ thống",
        content: "Phòng chat đã được tạo. Bạn có thể bắt đầu trò chuyện.",
        type: "SYSTEM",
        createdAt: Date.now() / 1000 - 86400 * 3, // 3 days ago
        read: true
    },
    {
        id: 1002,
        chatRoomId: 1,
        senderId: 2,
        senderName: "Nguyễn Văn A",
        content: "Xin chào, tôi đã đặt phòng tại khách sạn của bạn cho ngày 10/5/2025",
        type: "USER",
        createdAt: Date.now() / 1000 - 7200, // 2 hours ago
        read: true
    },
    {
        id: 1003,
        chatRoomId: 1,
        senderId: 1,
        senderName: "Chủ khách sạn",
        content: "Xin chào Nguyễn Văn A, chúng tôi đã nhận được đặt phòng của bạn và đã xác nhận. Bạn cần hỗ trợ gì không?",
        type: "USER",
        createdAt: Date.now() / 1000 - 5400, // 1.5 hours ago
        read: true
    },
    {
        id: 1004,
        chatRoomId: 1,
        senderId: 2,
        senderName: "Nguyễn Văn A",
        content: "Tôi có một số câu hỏi về phòng đặt. Phòng có view ra biển không?",
        type: "USER",
        createdAt: Date.now() / 1000 - 3600, // 1 hour ago
        read: false
    },
    {
        id: 1005,
        chatRoomId: 1,
        senderId: 2,
        senderName: "Nguyễn Văn A",
        content: "Và giờ check-in sớm nhất là mấy giờ?",
        type: "USER",
        createdAt: Date.now() / 1000 - 3500, // 58 minutes ago
        read: false
    },
    {
        id: 1006,
        chatRoomId: 1,
        senderId: 2,
        senderName: "Nguyễn Văn A",
        content: "Tôi sẽ đến sớm một chút và muốn biết có thể check-in sớm được không",
        type: "USER",
        createdAt: Date.now() / 1000 - 3400, // 57 minutes ago
        read: false
    }
];

// Mock messages for other rooms (can be expanded as needed)
const MOCK_MESSAGES = {
    1: MOCK_MESSAGES_ROOM_1,
    2: [
        {
            id: 2001,
            chatRoomId: 2,
            senderId: null,
            senderName: "Hệ thống",
            content: "Phòng chat đã được tạo. Bạn có thể bắt đầu trò chuyện.",
            type: "SYSTEM",
            createdAt: Date.now() / 1000 - 86400 * 5, // 5 days ago
            read: true
        },
        {
            id: 2002,
            chatRoomId: 2,
            senderId: 3,
            senderName: "Trần Thị B",
            content: "Chào bạn, tôi đã đặt phòng cho kỳ nghỉ tuần tới",
            type: "USER",
            createdAt: Date.now() / 1000 - 172800, // 2 days ago
            read: true
        },
        {
            id: 2003,
            chatRoomId: 2,
            senderId: 1,
            senderName: "Chủ khách sạn",
            content: "Xin chào, chúng tôi đã nhận được đặt phòng của bạn và đã xác nhận",
            type: "USER",
            createdAt: Date.now() / 1000 - 86500, // ~1 day ago
            read: true
        },
        {
            id: 2004,
            chatRoomId: 2,
            senderId: 3,
            senderName: "Trần Thị B",
            content: "Cảm ơn bạn đã xác nhận đặt phòng của tôi!",
            type: "USER",
            createdAt: Date.now() / 1000 - 86400, // 1 day ago
            read: true
        }
    ]
};

type ChatRoom = {
    id: number;
    bookingId: number;
    lastMessage?: {
        id: number;
        content: string;
        createdAt: number;
        senderName?: string;
        type: string;
    };
    participants: {
        userId: number;
        name: string;
        avatar?: string;
    }[];
    unreadCount: number;
};

type Message = {
    id: number;
    chatRoomId: number;
    senderId?: number | null;
    senderName?: string;
    content: string;
    type: string;
    createdAt: number;
    read: boolean;
};

export default function InboxPage() {
    const searchParams = useSearchParams();

    const [selectedRoom, setSelectedRoom] = useState<number | null>(null);
    const [chatRooms, setChatRooms] = useState<ChatRoom[]>([]);
    const [messages, setMessages] = useState<Message[]>([]);
    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(true);
    const [loadingMessages, setLoadingMessages] = useState(false);
    // Reference for auto-scrolling
    const messagesEndRef = useRef<HTMLDivElement>(null);
    const scrollAreaRef = useRef<HTMLDivElement>(null);

    // Mock user since we don't have AuthContext
    const user = MOCK_USER;

    const roomId = searchParams.get('roomId');

    // Scroll to bottom when messages change
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    // Load initial chat rooms with mock data
    useEffect(() => {
        // Simulate API loading delay
        const timer = setTimeout(() => {
            setChatRooms(MOCK_CHAT_ROOMS);
            setLoading(false);

            // If roomId is provided in URL, select that room
            if (roomId) {
                const roomIdNum = parseInt(roomId, 10);
                setSelectedRoom(roomIdNum);
                loadMessages(roomIdNum);
            }
        }, 1000);

        return () => clearTimeout(timer);
    }, [roomId]);

    // Load messages when a room is selected with mock data
    const loadMessages = (roomId: number) => {
        if (!roomId) return;

        setLoadingMessages(true);

        // Simulate API loading delay
        setTimeout(() => {
            const roomMessages = MOCK_MESSAGES[roomId as keyof typeof MOCK_MESSAGES] || [];
            setMessages(roomMessages);

            // Update unread count in chat rooms list
            setChatRooms(prev =>
                prev.map(room =>
                    room.id === roomId ? { ...room, unreadCount: 0 } : room
                )
            );

            setLoadingMessages(false);
        }, 800);
    };

    // Send message function with mock implementation
    const handleSendMessage = () => {
        if (!message.trim() || !selectedRoom) return;

        // Create a new mock message
        const newMessage: Message = {
            id: Math.floor(Math.random() * 10000) + 2000,
            chatRoomId: selectedRoom,
            senderId: user.id,
            senderName: user.name,
            content: message,
            type: "USER",
            createdAt: Math.floor(Date.now() / 1000),
            read: true
        };

        // Add message to the current conversation
        setMessages(prev => [...prev, newMessage]);

        // Update last message in the chat rooms list
        setChatRooms(prev =>
            prev.map(room =>
                room.id === selectedRoom ? {
                    ...room,
                    lastMessage: {
                        id: newMessage.id,
                        content: newMessage.content,
                        createdAt: newMessage.createdAt,
                        senderName: newMessage.senderName,
                        type: newMessage.type
                    }
                } : room
            )
        );

        // Clear input
        setMessage("");

        // Simulate reply after delay (for demo purposes)
        if (selectedRoom === 1) {
            setTimeout(() => {
                const replyMessage: Message = {
                    id: Math.floor(Math.random() * 10000) + 3000,
                    chatRoomId: selectedRoom,
                    senderId: 2,
                    senderName: "Nguyễn Văn A",
                    content: "Cảm ơn bạn đã trả lời. Tôi sẽ đến đúng giờ check-in.",
                    type: "USER",
                    createdAt: Math.floor(Date.now() / 1000),
                    read: true
                };

                setMessages(prev => [...prev, replyMessage]);

                // Also update in chat rooms
                setChatRooms(prev =>
                    prev.map(room =>
                        room.id === selectedRoom ? {
                            ...room,
                            lastMessage: {
                                id: replyMessage.id,
                                content: replyMessage.content,
                                createdAt: replyMessage.createdAt,
                                senderName: replyMessage.senderName,
                                type: replyMessage.type
                            },
                            unreadCount: room.unreadCount + 1
                        } : room
                    )
                );
            }, 3000);
        }
    };

    // Handle room selection
    const selectRoom = (roomId: number) => {
        setSelectedRoom(roomId);
        loadMessages(roomId);

        // Update URL with selected room ID
        const url = new URL(window.location.href);
        url.searchParams.set('roomId', roomId.toString());
        window.history.pushState({}, '', url.toString());
    };

    // Find other participant in the room (not the current user)
    const getOtherParticipant = (room: ChatRoom) => {
        return room.participants?.find(p => p.userId !== user?.id) || room.participants?.[0];
    };

    // Render selected room info
    const renderRoomHeader = () => {
        if (!selectedRoom) return null;

        const room = chatRooms.find(r => r.id === selectedRoom);
        if (!room) return null;

        const otherParticipant = getOtherParticipant(room);

        return (
            <div className="flex items-center p-4 border-b">
                <Button variant="ghost" size="icon" className="mr-2 md:hidden" onClick={() => setSelectedRoom(null)}>
                    <ChevronLeft className="h-5 w-5" />
                </Button>
                <Avatar className="h-10 w-10 mr-2">
                    <AvatarImage src={otherParticipant?.avatar || ''} alt={otherParticipant?.name} />
                    <AvatarFallback>{otherParticipant?.name?.charAt(0) || 'G'}</AvatarFallback>
                </Avatar>
                <div>
                    <div className="font-medium">{otherParticipant?.name}</div>
                    <div className="text-xs text-muted-foreground">
                        Booking ID: {room.bookingId}
                    </div>
                </div>
            </div>
        );
    };

    return (
        <div className="flex h-[calc(100vh-65px)]">
            {/* Chat list sidebar */}
            <div className={cn(
                "w-full md:w-1/3 lg:w-1/4 border-r",
                selectedRoom ? "hidden md:block" : "block"
            )}>
                <div className="p-4 border-b">
                    <h1 className="text-2xl font-bold">Tin nhắn</h1>
                </div>

                {loading ? (
                    <div className="flex justify-center items-center h-40">
                        <Loader2 className="h-8 w-8 animate-spin text-primary" />
                    </div>
                ) : chatRooms.length > 0 ? (
                    <ScrollArea className="h-[calc(100%-65px)]">
                        {chatRooms.map(room => {
                            const otherParticipant = getOtherParticipant(room);
                            return (
                                <ChatListItem
                                    key={room.id}
                                    active={room.id === selectedRoom}
                                    avatar={otherParticipant?.avatar}
                                    name={otherParticipant?.name || 'Guest'}
                                    message={room.lastMessage?.content || 'Start a conversation...'}
                                    time={room.lastMessage ? formatDistanceToNow(new Date(room.lastMessage.createdAt * 1000), {
                                        addSuffix: true,
                                        locale: vi
                                    }) : ''}
                                    unreadCount={room.unreadCount}
                                    onClick={() => selectRoom(room.id)}
                                />
                            );
                        })}
                    </ScrollArea>
                ) : (
                    <div className="flex flex-col items-center justify-center h-40 text-muted-foreground">
                        <p>Bạn chưa có cuộc trò chuyện nào</p>
                    </div>
                )}
            </div>

            {/* Chat area */}
            <div className={cn(
                "flex flex-col w-full md:w-2/3 lg:w-3/4",
                selectedRoom ? "block" : "hidden md:block"
            )}>
                {selectedRoom ? (
                    <>
                        {renderRoomHeader()}

                        <div className="flex flex-col h-[calc(100%-130px)] relative">
                            {loadingMessages ? (
                                <div className="flex-1 flex justify-center items-center">
                                    <Loader2 className="h-8 w-8 animate-spin text-primary" />
                                </div>
                            ) : (
                                <ScrollArea 
                                    ref={scrollAreaRef} 
                                    className="flex-1 p-4"
                                    style={{ height: '100%' }}
                                >
                                    <div className="space-y-4">
                                        {messages.map((msg) => (
                                            <ChatMessage
                                                key={msg.id}
                                                content={msg.content}
                                                timestamp={new Date(msg.createdAt * 1000)}
                                                isOwn={msg.senderId === user?.id}
                                                sender={msg.senderName}
                                                type={msg.type}
                                            />
                                        ))}
                                        <div ref={messagesEndRef} />
                                    </div>
                                </ScrollArea>
                            )}
                        </div>

                        <div className="p-4 border-t sticky bottom-0 bg-background">
                            <form
                                onSubmit={(e) => {
                                    e.preventDefault();
                                    handleSendMessage();
                                }}
                                className="flex items-center gap-2"
                            >
                                <Input
                                    placeholder="Nhập tin nhắn..."
                                    value={message}
                                    onChange={(e) => setMessage(e.target.value)}
                                    className="flex-1"
                                />
                                <Button type="submit" size="icon">
                                    <Send className="h-4 w-4" />
                                </Button>
                            </form>
                        </div>
                    </>
                ) : (
                    <div className="flex-1 flex flex-col items-center justify-center">
                        <h2 className="text-2xl font-medium mb-2">Chào mừng đến với tin nhắn</h2>
                        <p className="text-muted-foreground">Chọn một cuộc trò chuyện để bắt đầu</p>
                    </div>
                )}
            </div>
        </div>
    );
}