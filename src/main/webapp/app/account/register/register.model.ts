export class Registration {
  constructor(
    public login: string,
    public firstName: string,
    public lastName: string,
    public email: string,
    public password: string,
    public responsable: number,
    public livreur: number,
    public client: number,
    public langKey: string
  ) {}
}
