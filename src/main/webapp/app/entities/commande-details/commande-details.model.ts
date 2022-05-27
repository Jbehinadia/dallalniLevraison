import { ICommande } from 'app/entities/commande/commande.model';
import { IPlat } from 'app/entities/plat/plat.model';

export interface ICommandeDetails {
  id?: number;
  prix?: number | null;
  qty?: number | null;
  etat?: string | null;
  commande?: ICommande | null;
  plat?: IPlat | null;
}

export class CommandeDetails implements ICommandeDetails {
  constructor(
    public id?: number,
    public prix?: number | null,
    public qty?: number | null,
    public etat?: string | null,
    public commande?: ICommande | null,
    public plat?: IPlat | null
  ) {}
}

export function getCommandeDetailsIdentifier(commandeDetails: ICommandeDetails): number | undefined {
  return commandeDetails.id;
}
