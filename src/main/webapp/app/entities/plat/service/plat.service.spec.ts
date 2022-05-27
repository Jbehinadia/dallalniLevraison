import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPlat, Plat } from '../plat.model';

import { PlatService } from './plat.service';

describe('Plat Service', () => {
  let service: PlatService;
  let httpMock: HttpTestingController;
  let elemDefault: IPlat;
  let expectedResult: IPlat | IPlat[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PlatService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nomPlat: 'AAAAAAA',
      imagePath: 'AAAAAAA',
      prix: 0,
      remisePerc: 0,
      remiceVal: 0,
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

    it('should create a Plat', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Plat()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Plat', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomPlat: 'BBBBBB',
          imagePath: 'BBBBBB',
          prix: 1,
          remisePerc: 1,
          remiceVal: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Plat', () => {
      const patchObject = Object.assign(
        {
          remisePerc: 1,
          remiceVal: 1,
        },
        new Plat()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Plat', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomPlat: 'BBBBBB',
          imagePath: 'BBBBBB',
          prix: 1,
          remisePerc: 1,
          remiceVal: 1,
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

    it('should delete a Plat', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPlatToCollectionIfMissing', () => {
      it('should add a Plat to an empty array', () => {
        const plat: IPlat = { id: 123 };
        expectedResult = service.addPlatToCollectionIfMissing([], plat);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(plat);
      });

      it('should not add a Plat to an array that contains it', () => {
        const plat: IPlat = { id: 123 };
        const platCollection: IPlat[] = [
          {
            ...plat,
          },
          { id: 456 },
        ];
        expectedResult = service.addPlatToCollectionIfMissing(platCollection, plat);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Plat to an array that doesn't contain it", () => {
        const plat: IPlat = { id: 123 };
        const platCollection: IPlat[] = [{ id: 456 }];
        expectedResult = service.addPlatToCollectionIfMissing(platCollection, plat);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(plat);
      });

      it('should add only unique Plat to an array', () => {
        const platArray: IPlat[] = [{ id: 123 }, { id: 456 }, { id: 54183 }];
        const platCollection: IPlat[] = [{ id: 123 }];
        expectedResult = service.addPlatToCollectionIfMissing(platCollection, ...platArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const plat: IPlat = { id: 123 };
        const plat2: IPlat = { id: 456 };
        expectedResult = service.addPlatToCollectionIfMissing([], plat, plat2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(plat);
        expect(expectedResult).toContain(plat2);
      });

      it('should accept null and undefined values', () => {
        const plat: IPlat = { id: 123 };
        expectedResult = service.addPlatToCollectionIfMissing([], null, plat, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(plat);
      });

      it('should return initial array if no Plat is added', () => {
        const platCollection: IPlat[] = [{ id: 123 }];
        expectedResult = service.addPlatToCollectionIfMissing(platCollection, undefined, null);
        expect(expectedResult).toEqual(platCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
