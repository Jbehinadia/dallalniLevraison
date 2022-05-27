import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRestaurant, getRestaurantIdentifier } from '../restaurant.model';

export type EntityResponseType = HttpResponse<IRestaurant>;
export type EntityArrayResponseType = HttpResponse<IRestaurant[]>;

@Injectable({ providedIn: 'root' })
export class RestaurantService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/restaurants');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(restaurant: IRestaurant): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(restaurant);
    return this.http
      .post<IRestaurant>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(restaurant: IRestaurant): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(restaurant);
    return this.http
      .put<IRestaurant>(`${this.resourceUrl}/${getRestaurantIdentifier(restaurant) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(restaurant: IRestaurant): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(restaurant);
    return this.http
      .patch<IRestaurant>(`${this.resourceUrl}/${getRestaurantIdentifier(restaurant) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IRestaurant>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IRestaurant[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRestaurantToCollectionIfMissing(
    restaurantCollection: IRestaurant[],
    ...restaurantsToCheck: (IRestaurant | null | undefined)[]
  ): IRestaurant[] {
    const restaurants: IRestaurant[] = restaurantsToCheck.filter(isPresent);
    if (restaurants.length > 0) {
      const restaurantCollectionIdentifiers = restaurantCollection.map(restaurantItem => getRestaurantIdentifier(restaurantItem)!);
      const restaurantsToAdd = restaurants.filter(restaurantItem => {
        const restaurantIdentifier = getRestaurantIdentifier(restaurantItem);
        if (restaurantIdentifier == null || restaurantCollectionIdentifiers.includes(restaurantIdentifier)) {
          return false;
        }
        restaurantCollectionIdentifiers.push(restaurantIdentifier);
        return true;
      });
      return [...restaurantsToAdd, ...restaurantCollection];
    }
    return restaurantCollection;
  }

  protected convertDateFromClient(restaurant: IRestaurant): IRestaurant {
    return Object.assign({}, restaurant, {
      dateOuverture: restaurant.dateOuverture?.isValid() ? restaurant.dateOuverture.toJSON() : undefined,
      dateFermiture: restaurant.dateFermiture?.isValid() ? restaurant.dateFermiture.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateOuverture = res.body.dateOuverture ? dayjs(res.body.dateOuverture) : undefined;
      res.body.dateFermiture = res.body.dateFermiture ? dayjs(res.body.dateFermiture) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((restaurant: IRestaurant) => {
        restaurant.dateOuverture = restaurant.dateOuverture ? dayjs(restaurant.dateOuverture) : undefined;
        restaurant.dateFermiture = restaurant.dateFermiture ? dayjs(restaurant.dateFermiture) : undefined;
      });
    }
    return res;
  }
}
