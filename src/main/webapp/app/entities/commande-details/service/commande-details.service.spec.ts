import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICommandeDetails, CommandeDetails } from '../commande-details.model';

import { CommandeDetailsService } from './commande-details.service';

describe('CommandeDetails Service', () => {
  let service: CommandeDetailsService;
  let httpMock: HttpTestingController;
  let elemDefault: ICommandeDetails;
  let expectedResult: ICommandeDetails | ICommandeDetails[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CommandeDetailsService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      prix: 0,
      etat: 'AAAAAAA',
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

    it('should create a CommandeDetails', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new CommandeDetails()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CommandeDetails', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          prix: 1,
          etat: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CommandeDetails', () => {
      const patchObject = Object.assign(
        {
          etat: 'BBBBBB',
        },
        new CommandeDetails()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CommandeDetails', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          prix: 1,
          etat: 'BBBBBB',
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

    it('should delete a CommandeDetails', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCommandeDetailsToCollectionIfMissing', () => {
      it('should add a CommandeDetails to an empty array', () => {
        const commandeDetails: ICommandeDetails = { id: 123 };
        expectedResult = service.addCommandeDetailsToCollectionIfMissing([], commandeDetails);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(commandeDetails);
      });

      it('should not add a CommandeDetails to an array that contains it', () => {
        const commandeDetails: ICommandeDetails = { id: 123 };
        const commandeDetailsCollection: ICommandeDetails[] = [
          {
            ...commandeDetails,
          },
          { id: 456 },
        ];
        expectedResult = service.addCommandeDetailsToCollectionIfMissing(commandeDetailsCollection, commandeDetails);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CommandeDetails to an array that doesn't contain it", () => {
        const commandeDetails: ICommandeDetails = { id: 123 };
        const commandeDetailsCollection: ICommandeDetails[] = [{ id: 456 }];
        expectedResult = service.addCommandeDetailsToCollectionIfMissing(commandeDetailsCollection, commandeDetails);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(commandeDetails);
      });

      it('should add only unique CommandeDetails to an array', () => {
        const commandeDetailsArray: ICommandeDetails[] = [{ id: 123 }, { id: 456 }, { id: 84465 }];
        const commandeDetailsCollection: ICommandeDetails[] = [{ id: 123 }];
        expectedResult = service.addCommandeDetailsToCollectionIfMissing(commandeDetailsCollection, ...commandeDetailsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const commandeDetails: ICommandeDetails = { id: 123 };
        const commandeDetails2: ICommandeDetails = { id: 456 };
        expectedResult = service.addCommandeDetailsToCollectionIfMissing([], commandeDetails, commandeDetails2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(commandeDetails);
        expect(expectedResult).toContain(commandeDetails2);
      });

      it('should accept null and undefined values', () => {
        const commandeDetails: ICommandeDetails = { id: 123 };
        expectedResult = service.addCommandeDetailsToCollectionIfMissing([], null, commandeDetails, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(commandeDetails);
      });

      it('should return initial array if no CommandeDetails is added', () => {
        const commandeDetailsCollection: ICommandeDetails[] = [{ id: 123 }];
        expectedResult = service.addCommandeDetailsToCollectionIfMissing(commandeDetailsCollection, undefined, null);
        expect(expectedResult).toEqual(commandeDetailsCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
