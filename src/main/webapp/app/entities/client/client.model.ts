export interface IClient {
  id?: number;
  nomClient?: string | null;
  prenomClient?: string | null;
  adresseClient?: string | null;
  numClient?: string | null;
}

export class Client implements IClient {
  constructor(
    public id?: number,
    public nomClient?: string | null,
    public prenomClient?: string | null,
    public adresseClient?: string | null,
    public numClient?: string | null
  ) {}
}

export function getClientIdentifier(client: IClient): number | undefined {
  return client.id;
}
