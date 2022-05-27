export interface IResponsableRestaurant {
  id?: number;
  nomResponsable?: string | null;
  prenomResponsable?: string | null;
  adresseResponsable?: string | null;
  numResponsable?: string | null;
}

export class ResponsableRestaurant implements IResponsableRestaurant {
  constructor(
    public id?: number,
    public nomResponsable?: string | null,
    public prenomResponsable?: string | null,
    public adresseResponsable?: string | null,
    public numResponsable?: string | null
  ) {}
}

export function getResponsableRestaurantIdentifier(responsableRestaurant: IResponsableRestaurant): number | undefined {
  return responsableRestaurant.id;
}
