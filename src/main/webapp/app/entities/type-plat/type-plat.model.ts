export interface ITypePlat {
  id?: number;
  type?: string | null;
  imagePath?: string | null;
}

export class TypePlat implements ITypePlat {
  constructor(public id?: number, public type?: string | null, public imagePath?: string | null) {}
}

export function getTypePlatIdentifier(typePlat: ITypePlat): number | undefined {
  return typePlat.id;
}
