import * as dayjs from 'dayjs';
import { IResponsableRestaurant } from 'app/entities/responsable-restaurant/responsable-restaurant.model';

export interface IRestaurant {
  id?: number;
  nomRestaurant?: string | null;
  adresseRestaurant?: string | null;
  numRestaurant?: string | null;
  dateOuverture?: dayjs.Dayjs | null;
  dateFermiture?: dayjs.Dayjs | null;
  responsableRestaurant?: IResponsableRestaurant | null;
}

export class Restaurant implements IRestaurant {
  constructor(
    public id?: number,
    public nomRestaurant?: string | null,
    public adresseRestaurant?: string | null,
    public numRestaurant?: string | null,
    public dateOuverture?: dayjs.Dayjs | null,
    public dateFermiture?: dayjs.Dayjs | null,
    public responsableRestaurant?: IResponsableRestaurant | null
  ) {}
}

export function getRestaurantIdentifier(restaurant: IRestaurant): number | undefined {
  return restaurant.id;
}
