export interface ILivreur {
  id?: number;
  nomLivreur?: string | null;
  prenomLivreur?: string | null;
  adresseLivreur?: string | null;
  numLivreur?: string | null;
}

export class Livreur implements ILivreur {
  constructor(
    public id?: number,
    public nomLivreur?: string | null,
    public prenomLivreur?: string | null,
    public adresseLivreur?: string | null,
    public numLivreur?: string | null
  ) {}
}

export function getLivreurIdentifier(livreur: ILivreur): number | undefined {
  return livreur.id;
}
