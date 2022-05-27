import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IResponsableRestaurant, getResponsableRestaurantIdentifier } from '../responsable-restaurant.model';

export type EntityResponseType = HttpResponse<IResponsableRestaurant>;
export type EntityArrayResponseType = HttpResponse<IResponsableRestaurant[]>;

@Injectable({ providedIn: 'root' })
export class ResponsableRestaurantService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/responsable-restaurants');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(responsableRestaurant: IResponsableRestaurant): Observable<EntityResponseType> {
    return this.http.post<IResponsableRestaurant>(this.resourceUrl, responsableRestaurant, { observe: 'response' });
  }

  update(responsableRestaurant: IResponsableRestaurant): Observable<EntityResponseType> {
    return this.http.put<IResponsableRestaurant>(
      `${this.resourceUrl}/${getResponsableRestaurantIdentifier(responsableRestaurant) as number}`,
      responsableRestaurant,
      { observe: 'response' }
    );
  }

  partialUpdate(responsableRestaurant: IResponsableRestaurant): Observable<EntityResponseType> {
    return this.http.patch<IResponsableRestaurant>(
      `${this.resourceUrl}/${getResponsableRestaurantIdentifier(responsableRestaurant) as number}`,
      responsableRestaurant,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IResponsableRestaurant>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IResponsableRestaurant[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addResponsableRestaurantToCollectionIfMissing(
    responsableRestaurantCollection: IResponsableRestaurant[],
    ...responsableRestaurantsToCheck: (IResponsableRestaurant | null | undefined)[]
  ): IResponsableRestaurant[] {
    const responsableRestaurants: IResponsableRestaurant[] = responsableRestaurantsToCheck.filter(isPresent);
    if (responsableRestaurants.length > 0) {
      const responsableRestaurantCollectionIdentifiers = responsableRestaurantCollection.map(
        responsableRestaurantItem => getResponsableRestaurantIdentifier(responsableRestaurantItem)!
      );
      const responsableRestaurantsToAdd = responsableRestaurants.filter(responsableRestaurantItem => {
        const responsableRestaurantIdentifier = getResponsableRestaurantIdentifier(responsableRestaurantItem);
        if (
          responsableRestaurantIdentifier == null ||
          responsableRestaurantCollectionIdentifiers.includes(responsableRestaurantIdentifier)
        ) {
          return false;
        }
        responsableRestaurantCollectionIdentifiers.push(responsableRestaurantIdentifier);
        return true;
      });
      return [...responsableRestaurantsToAdd, ...responsableRestaurantCollection];
    }
    return responsableRestaurantCollection;
  }
}
