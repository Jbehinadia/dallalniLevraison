import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IResponsableRestaurant, ResponsableRestaurant } from '../responsable-restaurant.model';

import { ResponsableRestaurantService } from './responsable-restaurant.service';

describe('ResponsableRestaurant Service', () => {
  let service: ResponsableRestaurantService;
  let httpMock: HttpTestingController;
  let elemDefault: IResponsableRestaurant;
  let expectedResult: IResponsableRestaurant | IResponsableRestaurant[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ResponsableRestaurantService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nomResponsable: 'AAAAAAA',
      prenomResponsable: 'AAAAAAA',
      adresseResponsable: 'AAAAAAA',
      numResponsable: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a ResponsableRestaurant', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new ResponsableRestaurant()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ResponsableRestaurant', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomResponsable: 'BBBBBB',
          prenomResponsable: 'BBBBBB',
          adresseResponsable: 'BBBBBB',
          numResponsable: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ResponsableRestaurant', () => {
      const patchObject = Object.assign(
        {
          adresseResponsable: 'BBBBBB',
          numResponsable: 'BBBBBB',
        },
        new ResponsableRestaurant()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ResponsableRestaurant', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomResponsable: 'BBBBBB',
          prenomResponsable: 'BBBBBB',
          adresseResponsable: 'BBBBBB',
          numResponsable: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a ResponsableRestaurant', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addResponsableRestaurantToCollectionIfMissing', () => {
      it('should add a ResponsableRestaurant to an empty array', () => {
        const responsableRestaurant: IResponsableRestaurant = { id: 123 };
        expectedResult = service.addResponsableRestaurantToCollectionIfMissing([], responsableRestaurant);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(responsableRestaurant);
      });

      it('should not add a ResponsableRestaurant to an array that contains it', () => {
        const responsableRestaurant: IResponsableRestaurant = { id: 123 };
        const responsableRestaurantCollection: IResponsableRestaurant[] = [
          {
            ...responsableRestaurant,
          },
          { id: 456 },
        ];
        expectedResult = service.addResponsableRestaurantToCollectionIfMissing(responsableRestaurantCollection, responsableRestaurant);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ResponsableRestaurant to an array that doesn't contain it", () => {
        const responsableRestaurant: IResponsableRestaurant = { id: 123 };
        const responsableRestaurantCollection: IResponsableRestaurant[] = [{ id: 456 }];
        expectedResult = service.addResponsableRestaurantToCollectionIfMissing(responsableRestaurantCollection, responsableRestaurant);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(responsableRestaurant);
      });

      it('should add only unique ResponsableRestaurant to an array', () => {
        const responsableRestaurantArray: IResponsableRestaurant[] = [{ id: 123 }, { id: 456 }, { id: 42312 }];
        const responsableRestaurantCollection: IResponsableRestaurant[] = [{ id: 123 }];
        expectedResult = service.addResponsableRestaurantToCollectionIfMissing(
          responsableRestaurantCollection,
          ...responsableRestaurantArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const responsableRestaurant: IResponsableRestaurant = { id: 123 };
        const responsableRestaurant2: IResponsableRestaurant = { id: 456 };
        expectedResult = service.addResponsableRestaurantToCollectionIfMissing([], responsableRestaurant, responsableRestaurant2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(responsableRestaurant);
        expect(expectedResult).toContain(responsableRestaurant2);
      });

      it('should accept null and undefined values', () => {
        const responsableRestaurant: IResponsableRestaurant = { id: 123 };
        expectedResult = service.addResponsableRestaurantToCollectionIfMissing([], null, responsableRestaurant, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(responsableRestaurant);
      });

      it('should return initial array if no ResponsableRestaurant is added', () => {
        const responsableRestaurantCollection: IResponsableRestaurant[] = [{ id: 123 }];
        expectedResult = service.addResponsableRestaurantToCollectionIfMissing(responsableRestaurantCollection, undefined, null);
        expect(expectedResult).toEqual(responsableRestaurantCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
