import { IRestaurant } from 'app/entities/restaurant/restaurant.model';

export interface IMenu {
  id?: number;
  nomMenu?: string | null;
  restaurant?: IRestaurant | null;
}

export class Menu implements IMenu {
  constructor(public id?: number, public nomMenu?: string | null, public restaurant?: IRestaurant | null) {}
}

export function getMenuIdentifier(menu: IMenu): number | undefined {
  return menu.id;
}
