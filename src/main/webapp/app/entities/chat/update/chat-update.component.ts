import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { ChatService } from '../service/chat.service';
import { IChat } from '../chat.model';
import { ChatFormGroup, ChatFormService } from './chat-form.service';

@Component({
  selector: 'jhi-chat-update',
  templateUrl: './chat-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ChatUpdateComponent implements OnInit {
  isSaving = false;
  chat: IChat | null = null;

  usersSharedCollection: IUser[] = [];
  profilesSharedCollection: IProfile[] = [];

  protected chatService = inject(ChatService);
  protected chatFormService = inject(ChatFormService);
  protected userService = inject(UserService);
  protected profileService = inject(ProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ChatFormGroup = this.chatFormService.createChatFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareProfile = (o1: IProfile | null, o2: IProfile | null): boolean => this.profileService.compareProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ chat }) => {
      this.chat = chat;
      if (chat) {
        this.updateForm(chat);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const chat = this.chatFormService.getChat(this.editForm);
    if (chat.id !== null) {
      this.subscribeToSaveResponse(this.chatService.update(chat));
    } else {
      this.subscribeToSaveResponse(this.chatService.create(chat));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IChat>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
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

  protected updateForm(chat: IChat): void {
    this.chat = chat;
    this.chatFormService.resetForm(this.editForm, chat);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, chat.user);
    this.profilesSharedCollection = this.profileService.addProfileToCollectionIfMissing<IProfile>(
      this.profilesSharedCollection,
      ...(chat.profiles ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.chat?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.profileService
      .query()
      .pipe(map((res: HttpResponse<IProfile[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfile[]) =>
          this.profileService.addProfileToCollectionIfMissing<IProfile>(profiles, ...(this.chat?.profiles ?? [])),
        ),
      )
      .subscribe((profiles: IProfile[]) => (this.profilesSharedCollection = profiles));
  }
}
