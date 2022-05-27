import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITypePlat, TypePlat } from '../type-plat.model';
import { TypePlatService } from '../service/type-plat.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-type-plat-update',
  templateUrl: './type-plat-update.component.html',
})
export class TypePlatUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    type: [],
    imagePath: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected typePlatService: TypePlatService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ typePlat }) => {
      this.updateForm(typePlat);
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('dallalniLivraisonApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const typePlat = this.createFromForm();
    if (typePlat.id !== undefined) {
      this.subscribeToSaveResponse(this.typePlatService.update(typePlat));
    } else {
      this.subscribeToSaveResponse(this.typePlatService.create(typePlat));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITypePlat>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(typePlat: ITypePlat): void {
    this.editForm.patchValue({
      id: typePlat.id,
      type: typePlat.type,
      imagePath: typePlat.imagePath,
    });
  }

  protected createFromForm(): ITypePlat {
    return {
      ...new TypePlat(),
      id: this.editForm.get(['id'])!.value,
      type: this.editForm.get(['type'])!.value,
      imagePath: this.editForm.get(['imagePath'])!.value,
    };
  }
}
