import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITypePlat, TypePlat } from '../type-plat.model';

import { TypePlatService } from './type-plat.service';

describe('TypePlat Service', () => {
  let service: TypePlatService;
  let httpMock: HttpTestingController;
  let elemDefault: ITypePlat;
  let expectedResult: ITypePlat | ITypePlat[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TypePlatService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      type: 'AAAAAAA',
      imagePath: 'AAAAAAA',
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

    it('should create a TypePlat', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new TypePlat()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TypePlat', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          type: 'BBBBBB',
          imagePath: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TypePlat', () => {
      const patchObject = Object.assign(
        {
          type: 'BBBBBB',
          imagePath: 'BBBBBB',
        },
        new TypePlat()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TypePlat', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          type: 'BBBBBB',
          imagePath: 'BBBBBB',
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

    it('should delete a TypePlat', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTypePlatToCollectionIfMissing', () => {
      it('should add a TypePlat to an empty array', () => {
        const typePlat: ITypePlat = { id: 123 };
        expectedResult = service.addTypePlatToCollectionIfMissing([], typePlat);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(typePlat);
      });

      it('should not add a TypePlat to an array that contains it', () => {
        const typePlat: ITypePlat = { id: 123 };
        const typePlatCollection: ITypePlat[] = [
          {
            ...typePlat,
          },
          { id: 456 },
        ];
        expectedResult = service.addTypePlatToCollectionIfMissing(typePlatCollection, typePlat);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TypePlat to an array that doesn't contain it", () => {
        const typePlat: ITypePlat = { id: 123 };
        const typePlatCollection: ITypePlat[] = [{ id: 456 }];
        expectedResult = service.addTypePlatToCollectionIfMissing(typePlatCollection, typePlat);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(typePlat);
      });

      it('should add only unique TypePlat to an array', () => {
        const typePlatArray: ITypePlat[] = [{ id: 123 }, { id: 456 }, { id: 86904 }];
        const typePlatCollection: ITypePlat[] = [{ id: 123 }];
        expectedResult = service.addTypePlatToCollectionIfMissing(typePlatCollection, ...typePlatArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const typePlat: ITypePlat = { id: 123 };
        const typePlat2: ITypePlat = { id: 456 };
        expectedResult = service.addTypePlatToCollectionIfMissing([], typePlat, typePlat2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(typePlat);
        expect(expectedResult).toContain(typePlat2);
      });

      it('should accept null and undefined values', () => {
        const typePlat: ITypePlat = { id: 123 };
        expectedResult = service.addTypePlatToCollectionIfMissing([], null, typePlat, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(typePlat);
      });

      it('should return initial array if no TypePlat is added', () => {
        const typePlatCollection: ITypePlat[] = [{ id: 123 }];
        expectedResult = service.addTypePlatToCollectionIfMissing(typePlatCollection, undefined, null);
        expect(expectedResult).toEqual(typePlatCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
