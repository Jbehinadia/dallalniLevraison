import * as dayjs from 'dayjs';
import { IMenu } from 'app/entities/menu/menu.model';
import { ResponsableRestaurant } from '../responsable-restaurant/responsable-restaurant.model';

export interface IRestaurant {
  id?: number;
  nomRestaurant?: string | null;
  adresseRestaurant?: string | null;
  numRestaurant?: string | null;
  dateOuverture?: dayjs.Dayjs | null;
  dateFermiture?: dayjs.Dayjs | null;
  commandes?: IMenu[] | null;
  responsableRestaurant?: ResponsableRestaurant | null;

  actuelMenu?: string; //Local
}

export class Restaurant implements IRestaurant {
  constructor(
    public id?: number,
    public nomRestaurant?: string | null,
    public adresseRestaurant?: string | null,
    public numRestaurant?: string | null,
    public dateOuverture?: dayjs.Dayjs | null,
    public dateFermiture?: dayjs.Dayjs | null,
    public commandes?: IMenu[] | null,
    public responsableRestaurant?: ResponsableRestaurant | null
  ) {}
}

export function getRestaurantIdentifier(restaurant: IRestaurant): number | undefined {
  return restaurant.id;
}
