export interface Pin {
    pinId: number,
    userId?: number,
    locationName: string,
    description?:string,
    latitude: number,
    longitude: number,
    isSaved: boolean,
    isDraggable: boolean,
    iconUrl: string
}
