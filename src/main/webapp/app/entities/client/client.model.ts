import { ICommande } from 'app/entities/commande/commande.model';

export interface IClient {
  id?: number;
  nomClient?: string | null;
  prenomClient?: string | null;
  adresseClient?: string | null;
  numClient?: string | null;
  commandes?: ICommande[] | null;
}

export class Client implements IClient {
  constructor(
    public id?: number,
    public nomClient?: string | null,
    public prenomClient?: string | null,
    public adresseClient?: string | null,
    public numClient?: string | null,
    public commandes?: ICommande[] | null
  ) {}
}

export function getClientIdentifier(client: IClient): number | undefined {
  return client.id;
}
