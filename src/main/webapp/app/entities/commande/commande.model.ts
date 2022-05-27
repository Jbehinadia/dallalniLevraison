import * as dayjs from 'dayjs';
import { ILivreur } from 'app/entities/livreur/livreur.model';
import { IClient } from 'app/entities/client/client.model';

export interface ICommande {
  id?: number;
  adresseCommande?: string | null;
  etat?: string | null;
  dateCommande?: dayjs.Dayjs | null;
  prixTotal?: number | null;
  remisePerc?: number | null;
  remiceVal?: number | null;
  prixLivreson?: number | null;
  dateSortie?: dayjs.Dayjs | null;
  livreur?: ILivreur | null;
  client?: IClient | null;
}

export class Commande implements ICommande {
  constructor(
    public id?: number,
    public adresseCommande?: string | null,
    public etat?: string | null,
    public dateCommande?: dayjs.Dayjs | null,
    public prixTotal?: number | null,
    public remisePerc?: number | null,
    public remiceVal?: number | null,
    public prixLivreson?: number | null,
    public dateSortie?: dayjs.Dayjs | null,
    public livreur?: ILivreur | null,
    public client?: IClient | null
  ) {}
}

export function getCommandeIdentifier(commande: ICommande): number | undefined {
  return commande.id;
}
