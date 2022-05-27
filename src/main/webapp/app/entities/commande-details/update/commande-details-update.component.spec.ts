import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CommandeDetailsService } from '../service/commande-details.service';
import { ICommandeDetails, CommandeDetails } from '../commande-details.model';
import { ICommande } from 'app/entities/commande/commande.model';
import { CommandeService } from 'app/entities/commande/service/commande.service';
import { IPlat } from 'app/entities/plat/plat.model';
import { PlatService } from 'app/entities/plat/service/plat.service';

import { CommandeDetailsUpdateComponent } from './commande-details-update.component';

describe('CommandeDetails Management Update Component', () => {
  let comp: CommandeDetailsUpdateComponent;
  let fixture: ComponentFixture<CommandeDetailsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let commandeDetailsService: CommandeDetailsService;
  let commandeService: CommandeService;
  let platService: PlatService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CommandeDetailsUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CommandeDetailsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CommandeDetailsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    commandeDetailsService = TestBed.inject(CommandeDetailsService);
    commandeService = TestBed.inject(CommandeService);
    platService = TestBed.inject(PlatService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Commande query and add missing value', () => {
      const commandeDetails: ICommandeDetails = { id: 456 };
      const commande: ICommande = { id: 2572 };
      commandeDetails.commande = commande;

      const commandeCollection: ICommande[] = [{ id: 30085 }];
      jest.spyOn(commandeService, 'query').mockReturnValue(of(new HttpResponse({ body: commandeCollection })));
      const additionalCommandes = [commande];
      const expectedCollection: ICommande[] = [...additionalCommandes, ...commandeCollection];
      jest.spyOn(commandeService, 'addCommandeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ commandeDetails });
      comp.ngOnInit();

      expect(commandeService.query).toHaveBeenCalled();
      expect(commandeService.addCommandeToCollectionIfMissing).toHaveBeenCalledWith(commandeCollection, ...additionalCommandes);
      expect(comp.commandesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Plat query and add missing value', () => {
      const commandeDetails: ICommandeDetails = { id: 456 };
      const plat: IPlat = { id: 86021 };
      commandeDetails.plat = plat;

      const platCollection: IPlat[] = [{ id: 37903 }];
      jest.spyOn(platService, 'query').mockReturnValue(of(new HttpResponse({ body: platCollection })));
      const additionalPlats = [plat];
      const expectedCollection: IPlat[] = [...additionalPlats, ...platCollection];
      jest.spyOn(platService, 'addPlatToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ commandeDetails });
      comp.ngOnInit();

      expect(platService.query).toHaveBeenCalled();
      expect(platService.addPlatToCollectionIfMissing).toHaveBeenCalledWith(platCollection, ...additionalPlats);
      expect(comp.platsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const commandeDetails: ICommandeDetails = { id: 456 };
      const commande: ICommande = { id: 62288 };
      commandeDetails.commande = commande;
      const plat: IPlat = { id: 50472 };
      commandeDetails.plat = plat;

      activatedRoute.data = of({ commandeDetails });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(commandeDetails));
      expect(comp.commandesSharedCollection).toContain(commande);
      expect(comp.platsSharedCollection).toContain(plat);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CommandeDetails>>();
      const commandeDetails = { id: 123 };
      jest.spyOn(commandeDetailsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commandeDetails });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: commandeDetails }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(commandeDetailsService.update).toHaveBeenCalledWith(commandeDetails);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CommandeDetails>>();
      const commandeDetails = new CommandeDetails();
      jest.spyOn(commandeDetailsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commandeDetails });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: commandeDetails }));
      saveSubject.complete();

      // THEN
      expect(commandeDetailsService.create).toHaveBeenCalledWith(commandeDetails);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CommandeDetails>>();
      const commandeDetails = { id: 123 };
      jest.spyOn(commandeDetailsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commandeDetails });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(commandeDetailsService.update).toHaveBeenCalledWith(commandeDetails);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackCommandeById', () => {
      it('Should return tracked Commande primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCommandeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackPlatById', () => {
      it('Should return tracked Plat primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPlatById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
