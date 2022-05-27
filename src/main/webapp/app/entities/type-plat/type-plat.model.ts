import { IPlat } from 'app/entities/plat/plat.model';

export interface ITypePlat {
  id?: number;
  type?: string | null;
  imagePath?: string | null;
  plats?: IPlat[] | null;
}

export class TypePlat implements ITypePlat {
  constructor(public id?: number, public type?: string | null, public imagePath?: string | null, public plats?: IPlat[] | null) {}
}

export function getTypePlatIdentifier(typePlat: ITypePlat): number | undefined {
  return typePlat.id;
}
