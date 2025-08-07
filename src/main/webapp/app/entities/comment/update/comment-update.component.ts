import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMessage } from 'app/entities/message/message.model';
import { MessageService } from 'app/entities/message/service/message.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IPost } from 'app/entities/post/post.model';
import { PostService } from 'app/entities/post/service/post.service';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { CommentService } from '../service/comment.service';
import { IComment } from '../comment.model';
import { CommentFormGroup, CommentFormService } from './comment-form.service';

@Component({
  selector: 'jhi-comment-update',
  templateUrl: './comment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CommentUpdateComponent implements OnInit {
  isSaving = false;
  comment: IComment | null = null;

  parentsCollection: IMessage[] = [];
  usersSharedCollection: IUser[] = [];
  postsSharedCollection: IPost[] = [];
  profilesSharedCollection: IProfile[] = [];

  protected commentService = inject(CommentService);
  protected commentFormService = inject(CommentFormService);
  protected messageService = inject(MessageService);
  protected userService = inject(UserService);
  protected postService = inject(PostService);
  protected profileService = inject(ProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CommentFormGroup = this.commentFormService.createCommentFormGroup();

  compareMessage = (o1: IMessage | null, o2: IMessage | null): boolean => this.messageService.compareMessage(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  comparePost = (o1: IPost | null, o2: IPost | null): boolean => this.postService.comparePost(o1, o2);

  compareProfile = (o1: IProfile | null, o2: IProfile | null): boolean => this.profileService.compareProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ comment }) => {
      this.comment = comment;
      if (comment) {
        this.updateForm(comment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const comment = this.commentFormService.getComment(this.editForm);
    if (comment.id !== null) {
      this.subscribeToSaveResponse(this.commentService.update(comment));
    } else {
      this.subscribeToSaveResponse(this.commentService.create(comment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IComment>>): void {
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

  protected updateForm(comment: IComment): void {
    this.comment = comment;
    this.commentFormService.resetForm(this.editForm, comment);

    this.parentsCollection = this.messageService.addMessageToCollectionIfMissing<IMessage>(this.parentsCollection, comment.parent);
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, comment.user);
    this.postsSharedCollection = this.postService.addPostToCollectionIfMissing<IPost>(this.postsSharedCollection, comment.post);
    this.profilesSharedCollection = this.profileService.addProfileToCollectionIfMissing<IProfile>(
      this.profilesSharedCollection,
      comment.profile,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.messageService
      .query({ filter: 'comment-is-null' })
      .pipe(map((res: HttpResponse<IMessage[]>) => res.body ?? []))
      .pipe(map((messages: IMessage[]) => this.messageService.addMessageToCollectionIfMissing<IMessage>(messages, this.comment?.parent)))
      .subscribe((messages: IMessage[]) => (this.parentsCollection = messages));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.comment?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.postService
      .query()
      .pipe(map((res: HttpResponse<IPost[]>) => res.body ?? []))
      .pipe(map((posts: IPost[]) => this.postService.addPostToCollectionIfMissing<IPost>(posts, this.comment?.post)))
      .subscribe((posts: IPost[]) => (this.postsSharedCollection = posts));

    this.profileService
      .query()
      .pipe(map((res: HttpResponse<IProfile[]>) => res.body ?? []))
      .pipe(map((profiles: IProfile[]) => this.profileService.addProfileToCollectionIfMissing<IProfile>(profiles, this.comment?.profile)))
      .subscribe((profiles: IProfile[]) => (this.profilesSharedCollection = profiles));
  }
}
