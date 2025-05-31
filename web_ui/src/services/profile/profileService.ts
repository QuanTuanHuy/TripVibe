import apiClient from '../api.client';
import { UserProfile, UpdateProfileDto, Friend, CreateFriendDto } from '../../types/profile/profile.types';

// Path to profile service through API Gateway
const PROFILE_PATH = '/profile_service/api/public/v1';

export const profileService = {
    // User Profile Methods
    getUserProfile: async (): Promise<UserProfile> => {
        return apiClient.get<UserProfile>(`${PROFILE_PATH}/tourists/me`);
    },

    updateUserProfile: async (data: UpdateProfileDto): Promise<UserProfile> => {
        return apiClient.put<UserProfile>(`${PROFILE_PATH}/tourists/me`, data);
    },

    updateAvatar: async (file: File): Promise<void> => {
        const formData = new FormData();
        formData.append('file', file);
        return apiClient.post(`${PROFILE_PATH}/tourists/me/avatar`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    },

    // Friends / Travel Companions Methods
    getFriends: async (): Promise<Friend[]> => {
        return apiClient.get<Friend[]>(`${PROFILE_PATH}/friends`);
    },

    createFriend: async (data: CreateFriendDto): Promise<Friend> => {
        return apiClient.post<Friend>(`${PROFILE_PATH}/friends`, data);
    },

    updateFriend: async (friendId: number, data: CreateFriendDto): Promise<Friend> => {
        return apiClient.put<Friend>(`${PROFILE_PATH}/friends/${friendId}`, data);
    },

    deleteFriend: async (friendId: number): Promise<void> => {
        return apiClient.delete(`${PROFILE_PATH}/friends/${friendId}`);
    },
};