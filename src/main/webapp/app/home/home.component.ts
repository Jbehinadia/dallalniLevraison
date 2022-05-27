import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { ITypePlat } from 'app/entities/type-plat/type-plat.model';
import { HttpResponse } from '@angular/common/http';
import { TypePlatService } from 'app/entities/type-plat/service/type-plat.service';
import { IPlat } from 'app/entities/plat/plat.model';
import { PlatService } from 'app/entities/plat/service/plat.service';
import { MenuService } from 'app/entities/menu/service/menu.service';
import { IMenu } from 'app/entities/menu/menu.model';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';
import { Restaurant } from 'app/entities/restaurant/restaurant.model';
import { ICommande } from 'app/entities/commande/commande.model';
import { ICommandeDetails } from 'app/entities/commande-details/commande-details.model';
import { CommandeService } from 'app/entities/commande/service/commande.service';
import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';
import Swals2 from 'sweetalert2';
import * as dayjs from 'dayjs';
import { CommandeDetailsService } from 'app/entities/commande-details/service/commande-details.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LoginComponent } from 'app/login/login.component';
import { CommandePourClientComponent } from 'app/home/list-pour-client/commande-pour-client.component';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  client: IClient | null = null;
  totalCommande = 0;
  typePlats?: ITypePlat[] = [];
  originPlats?: IPlat[] = [];
  Plats?: IPlat[] = [];
  linesCmd?: ICommandeDetails[] = [];
  nbrCommandes = 0;
  modalRefSignIn!: any;
  modalRef!: any;
  account!: Account;

  private readonly destroy$ = new Subject<void>();

  constructor(
    protected commandeService: CommandeService,
    protected commandeDetailsService: CommandeDetailsService,
    protected clientService: ClientService,
    protected restaurantService: RestaurantService,
    protected typePlatService: TypePlatService,
    protected platService: PlatService,
    protected menuService: MenuService,
    protected modalService: NgbModal,
    private accountService: AccountService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.totalCommande = 0;
    this.getTypePlats();
    this.getAllPlats();
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => {
        this.account = account!;
        this.getClientAndCommandes(account!);
      });
  }

  getClientAndCommandes(account: Account): void {
    this.clientService.find(account.client!).subscribe((resClient: HttpResponse<IClient>) => {
      this.client = resClient.body!;
      this.commandeService
        .query({
          'clientId.equals': this.client.id,
          size: 500,
        })
        .subscribe(resCmd => (this.nbrCommandes = resCmd.body!.length));
    });
  }

  getAllPlats(): void {
    this.originPlats = [];
    this.Plats = [];
    this.platService.query({ size: 1000 }).subscribe((resPlats: HttpResponse<IPlat[]>) => {
      this.originPlats = resPlats.body!;
      this.Plats = this.originPlats;
      this.Plats.forEach(plat => {
        if (plat.typePlat!.id) {
          this.menuService.find(plat.menu!.id!).subscribe((resMenu: HttpResponse<IMenu>) => {
            plat.menu!.nomMenu = resMenu.body!.nomMenu;
            if (resMenu.body!.restaurant!.id) {
              this.restaurantService.find(resMenu.body!.restaurant!.id).subscribe((resRestau: HttpResponse<Restaurant>) => {
                plat.nomRestau = resRestau.body?.nomRestaurant;
              });
            }
          });
        }
      });
    });
  }

  getTypePlats(): void {
    this.typePlats = [];
    this.typePlatService.query().subscribe((res: HttpResponse<ITypePlat[]>) => {
      this.typePlats = res.body ?? [];
    });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  filterPlatByType(typeID: number): void {
    this.Plats = this.originPlats;
    if (typeID) {
      this.Plats = this.Plats?.filter(plat => plat.typePlat?.id === typeID);
    }
  }

  addLineCmd(plat: IPlat): void {
    this.totalCommande = 0;
    const index = this.linesCmd!.findIndex(lineC => lineC.plat!.id === plat.id);
    if (index === -1) {
      const newLine: ICommandeDetails = {};
      newLine.plat = plat;
      newLine.qte = 1;
      newLine.prix = plat.remisePerc ? plat.prix! - (plat.prix! * plat.remisePerc) / 100 : plat.prix;
      this.linesCmd!.push(newLine);
    } else {
      const line = this.linesCmd!.find(lineC => lineC.plat!.id === plat.id);
      line!.qte! += 1;
    }

    this.linesCmd!.forEach(lineC => {
      this.totalCommande += lineC.prix! * lineC.qte!;
    });
  }

  deleteLineCmd(line: ICommandeDetails): void {
    this.totalCommande = 0;
    const index = this.linesCmd!.findIndex(lineC => lineC.plat!.id === line.plat!.id);
    if (index !== -1) {
      this.linesCmd!.splice(index, 1);
    }

    this.linesCmd!.forEach(lineC => {
      this.totalCommande += lineC.prix! * lineC.qte!;
    });
  }

  changeQties(): void {
    this.totalCommande = 0;
    this.linesCmd!.forEach(lineC => {
      lineC.prix = lineC.plat!.remisePerc ? lineC.plat!.prix! - (lineC.plat!.prix! * lineC.plat!.remisePerc) / 100 : lineC.plat!.prix;
      this.totalCommande += lineC.prix! * lineC.qte!;
    });
  }

  saveCommande(): void {
    if (this.client?.id) {
      if (!this.linesCmd!.length) {
        Swals2.fire({
          text: "S'il vous plait, choisissez au moins un plat!",
          type: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#ff8200',
        }).then();
      } else {
        Swals2.fire({
          title: 'Commande ' + this.totalCommande.toString() + ' DT',
          text: 'vous êtes sûr de vouloir passer Commande?',
          type: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#ff8200',
        }).then(result => {
          if (result.value) {
            const commande: ICommande = {};
            commande.client = this.client;
            commande.adresseCommande = this.client!.adresseClient;
            commande.etat = 'reprise';
            commande.dateCommande = dayjs(new Date());
            commande.prixTotal = this.totalCommande;
            commande.prixLivreson = 3;
            commande.dateSortie = dayjs(new Date().setHours(new Date().getHours() + 1));
            this.commandeService
              .create(commande)
              .pipe(map(res => res.body))
              .subscribe(resCmd => {
                this.nbrCommandes += 1;
                this.linesCmd!.forEach(lineC => {
                  lineC.commande = resCmd!;
                  lineC.etat = 'reprise';
                  this.commandeDetailsService.create(lineC).subscribe();
                });
                Swals2.fire({
                  title: 'Commande Reprise',
                  html: 'Commande Reprise avec succès',
                  type: 'success',
                }).then(() => {
                  this.linesCmd = [];
                  this.totalCommande = 0;
                });
              });
          }
        });
      }
    } else {
      this.modalRefSignIn = this.modalService.open(LoginComponent, { size: 'lg' });
      this.modalRefSignIn.componentInstance.parent = this;
    }
  }

  openMyCommandes(): void {
    this.modalRef = this.modalService.open(CommandePourClientComponent as Component, { size: 'lg' });
    this.modalRef.componentInstance.parent = this;
    this.modalRef.componentInstance.client = this.client!;
  }
}
