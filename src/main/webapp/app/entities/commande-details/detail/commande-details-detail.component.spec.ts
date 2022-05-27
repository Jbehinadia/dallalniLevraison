import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CommandeDetailsDetailComponent } from './commande-details-detail.component';

describe('CommandeDetails Management Detail Component', () => {
  let comp: CommandeDetailsDetailComponent;
  let fixture: ComponentFixture<CommandeDetailsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CommandeDetailsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ commandeDetails: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CommandeDetailsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CommandeDetailsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load commandeDetails on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.commandeDetails).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
