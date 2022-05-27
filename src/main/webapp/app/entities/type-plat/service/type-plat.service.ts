import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITypePlat, getTypePlatIdentifier } from '../type-plat.model';

export type EntityResponseType = HttpResponse<ITypePlat>;
export type EntityArrayResponseType = HttpResponse<ITypePlat[]>;

@Injectable({ providedIn: 'root' })
export class TypePlatService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/type-plats');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(typePlat: ITypePlat): Observable<EntityResponseType> {
    return this.http.post<ITypePlat>(this.resourceUrl, typePlat, { observe: 'response' });
  }

  update(typePlat: ITypePlat): Observable<EntityResponseType> {
    return this.http.put<ITypePlat>(`${this.resourceUrl}/${getTypePlatIdentifier(typePlat) as number}`, typePlat, { observe: 'response' });
  }

  partialUpdate(typePlat: ITypePlat): Observable<EntityResponseType> {
    return this.http.patch<ITypePlat>(`${this.resourceUrl}/${getTypePlatIdentifier(typePlat) as number}`, typePlat, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITypePlat>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITypePlat[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTypePlatToCollectionIfMissing(typePlatCollection: ITypePlat[], ...typePlatsToCheck: (ITypePlat | null | undefined)[]): ITypePlat[] {
    const typePlats: ITypePlat[] = typePlatsToCheck.filter(isPresent);
    if (typePlats.length > 0) {
      const typePlatCollectionIdentifiers = typePlatCollection.map(typePlatItem => getTypePlatIdentifier(typePlatItem)!);
      const typePlatsToAdd = typePlats.filter(typePlatItem => {
        const typePlatIdentifier = getTypePlatIdentifier(typePlatItem);
        if (typePlatIdentifier == null || typePlatCollectionIdentifiers.includes(typePlatIdentifier)) {
          return false;
        }
        typePlatCollectionIdentifiers.push(typePlatIdentifier);
        return true;
      });
      return [...typePlatsToAdd, ...typePlatCollection];
    }
    return typePlatCollection;
  }
}
