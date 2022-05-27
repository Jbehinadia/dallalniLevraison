import { Component, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';

import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE } from 'app/config/error.constants';
import { RegisterService } from './register.service';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { Observable } from 'rxjs';
import { IClient } from 'app/entities/client/client.model';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';
import { ResponsableRestaurantService } from 'app/entities/responsable-restaurant/service/responsable-restaurant.service';
import { ClientService } from 'app/entities/client/service/client.service';
import { LivreurService } from 'app/entities/livreur/service/livreur.service';
import { IResponsableRestaurant } from 'app/entities/responsable-restaurant/responsable-restaurant.model';
import { ILivreur } from 'app/entities/livreur/livreur.model';
import { IUser, } from 'app/admin/user-management/user-management.model';
import * as dayjs from 'dayjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'jhi-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.scss'],
})
export class RegisterComponent implements AfterViewInit {
  @ViewChild('login', { static: false })
  login?: ElementRef;

  doNotMatch = false;
  error = false;
  errorEmailExists = false;
  errorUserExists = false;
  success = false;
  user: IUser = {};

  typeUser = 'Client';
  resto: IRestaurant = {};
  client: IClient = {};
  responsable: IResponsableRestaurant = {};
  livreur: ILivreur = {};

  constructor(
    private registerService: RegisterService,
    private restaurantService: RestaurantService,
    private responsableRestaurantService: ResponsableRestaurantService,
    private clientService: ClientService,
    private livreurService: LivreurService,
    private fb: FormBuilder
  ) {}

  ngAfterViewInit(): void {
    if (this.login) {
      this.login.nativeElement.focus();
    }
    this.resto = {};
    this.client = {};
    this.responsable = {};
    this.livreur = {};
  }

  typeUUserChenged(type: string): void {
    this.typeUser = type;
    /* eslint-disable no-console */
    console.log(this.typeUser);
    /* eslint-enable no-console */
  }

  register(): void {
    this.doNotMatch = false;
    this.error = false;
    this.errorEmailExists = false;
    this.errorUserExists = false;

    if (this.user.password !== this.user.confirmPassword) {
      this.doNotMatch = true;
    } else {
      switch (this.typeUser) {
        case 'Client':
          this.createClient()
            .pipe(map((res: HttpResponse<IClient>) => res.body))
            .subscribe(client => {
              this.client = client!;
              this.registerUser();
            });
          break;
        case 'Livreur':
          this.createLivreur()
            .pipe(map(res => res.body))
            .subscribe(livreur => {
              this.livreur = livreur!;
              this.registerUser();
            });
          break;
        case 'Responsable':
          this.createResponsable()
            .pipe(map(res => res.body))
            .subscribe(responsable => {
              this.responsable = responsable!;
              this.resto.responsableRestaurant = responsable!;
              this.resto.dateOuverture = dayjs(new Date());
              this.resto.dateFermiture = dayjs(new Date());
              this.restaurantService.create(this.resto).subscribe(() => this.registerUser());
            });
          break;
      }
    }
  }

  createClient(): Observable<HttpResponse<IClient>> {
    this.client.nomClient = this.user.firstName!;
    this.client.prenomClient = this.user.lastName!;
    return this.clientService.create(this.client);
  }

  createLivreur(): Observable<HttpResponse<ILivreur>> {
    this.livreur.nomLivreur = this.user.firstName!;
    this.livreur.prenomLivreur = this.user.lastName!;
    return this.livreurService.create(this.livreur);
  }

  createResponsable(): Observable<HttpResponse<IResponsableRestaurant>> {
    this.responsable.nomResponsable = this.user.firstName!;
    this.responsable.prenomResponsable = this.user.lastName!;
    this.responsable.adresseResponsable = this.resto.adresseRestaurant!;
    this.responsable.numResponsable = this.resto.numRestaurant!;
    return this.responsableRestaurantService.create(this.responsable);
  }

  registerUser(): void {
    const login = this.user.login!;
    const firstName = this.user.firstName!;
    const lastName = this.user.lastName!;
    const email = this.user.email!;
    const password = this.user.password!;

    const responsable = this.responsable.id!;
    const livreur = this.livreur.id!;
    const client = this.client.id!;

    this.registerService
      .save({ login, firstName, lastName, email, password, responsable, livreur, client, langKey: 'en' })
      .subscribe({ next: () => (this.success = true), error: response => this.processError(response) });
  }

  private processError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === LOGIN_ALREADY_USED_TYPE) {
      this.errorUserExists = true;
    } else if (response.status === 400 && response.error.type === EMAIL_ALREADY_USED_TYPE) {
      this.errorEmailExists = true;
    } else {
      this.error = true;
    }
  }
}
