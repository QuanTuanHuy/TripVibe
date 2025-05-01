export interface Image {
    id: number;
    url: string;
    entityId?: number;
    entityType?: string;
    isPrimary?: boolean;
    version?: Map<string, string>;
}