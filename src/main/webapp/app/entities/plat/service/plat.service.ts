import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPlat, getPlatIdentifier } from '../plat.model';

export type EntityResponseType = HttpResponse<IPlat>;
export type EntityArrayResponseType = HttpResponse<IPlat[]>;

@Injectable({ providedIn: 'root' })
export class PlatService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/plats');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(plat: IPlat): Observable<EntityResponseType> {
    return this.http.post<IPlat>(this.resourceUrl, plat, { observe: 'response' });
  }

  update(plat: IPlat): Observable<EntityResponseType> {
    return this.http.put<IPlat>(`${this.resourceUrl}/${getPlatIdentifier(plat) as number}`, plat, { observe: 'response' });
  }

  partialUpdate(plat: IPlat): Observable<EntityResponseType> {
    return this.http.patch<IPlat>(`${this.resourceUrl}/${getPlatIdentifier(plat) as number}`, plat, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPlat>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPlat[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPlatToCollectionIfMissing(platCollection: IPlat[], ...platsToCheck: (IPlat | null | undefined)[]): IPlat[] {
    const plats: IPlat[] = platsToCheck.filter(isPresent);
    if (plats.length > 0) {
      const platCollectionIdentifiers = platCollection.map(platItem => getPlatIdentifier(platItem)!);
      const platsToAdd = plats.filter(platItem => {
        const platIdentifier = getPlatIdentifier(platItem);
        if (platIdentifier == null || platCollectionIdentifiers.includes(platIdentifier)) {
          return false;
        }
        platCollectionIdentifiers.push(platIdentifier);
        return true;
      });
      return [...platsToAdd, ...platCollection];
    }
    return platCollection;
  }
}
