import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IChat } from 'app/entities/chat/chat.model';
import { ChatService } from 'app/entities/chat/service/chat.service';
import { MessageService } from '../service/message.service';
import { IMessage } from '../message.model';
import { MessageFormGroup, MessageFormService } from './message-form.service';

@Component({
  selector: 'jhi-message-update',
  templateUrl: './message-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MessageUpdateComponent implements OnInit {
  isSaving = false;
  message: IMessage | null = null;

  usersSharedCollection: IUser[] = [];
  chatsSharedCollection: IChat[] = [];

  protected messageService = inject(MessageService);
  protected messageFormService = inject(MessageFormService);
  protected userService = inject(UserService);
  protected chatService = inject(ChatService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MessageFormGroup = this.messageFormService.createMessageFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareChat = (o1: IChat | null, o2: IChat | null): boolean => this.chatService.compareChat(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ message }) => {
      this.message = message;
      if (message) {
        this.updateForm(message);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const message = this.messageFormService.getMessage(this.editForm);
    if (message.id !== null) {
      this.subscribeToSaveResponse(this.messageService.update(message));
    } else {
      this.subscribeToSaveResponse(this.messageService.create(message));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMessage>>): void {
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

  protected updateForm(message: IMessage): void {
    this.message = message;
    this.messageFormService.resetForm(this.editForm, message);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, message.user);
    this.chatsSharedCollection = this.chatService.addChatToCollectionIfMissing<IChat>(this.chatsSharedCollection, message.chat);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.message?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.chatService
      .query()
      .pipe(map((res: HttpResponse<IChat[]>) => res.body ?? []))
      .pipe(map((chats: IChat[]) => this.chatService.addChatToCollectionIfMissing<IChat>(chats, this.message?.chat)))
      .subscribe((chats: IChat[]) => (this.chatsSharedCollection = chats));
  }
}
