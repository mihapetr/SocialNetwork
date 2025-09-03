import { Component, inject, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DataUtils } from 'app/core/util/data-util.service';
import { IProfile } from '../profile.model';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { IPost } from '../../post/post.model';
import { IChat } from '../../chat/chat.model';
import { ProfileService } from '../service/profile.service';
import { AccountService } from '../../../core/auth/account.service';
import FormatMediumDatetimePipe from '../../../shared/date/format-medium-datetime.pipe';
import FormatMediumDatePipe from '../../../shared/date/format-medium-date.pipe';
import dayjs from 'dayjs/esm';

@Component({
  selector: 'jhi-profile-detail',
  templateUrl: './profile-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ProfileDetailComponent {
  profile = input<IProfile | null>(null);
  account = inject(AccountService).trackCurrentAccount();

  map = new Map<string, IProfile>();
  requested = false;

  protected dataUtils = inject(DataUtils);
  protected profileService = inject(ProfileService);

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }

  formatTime(time: string): string {
    const date = new Date(time);

    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0'); // months are 0-based
    const day = date.getDate().toString().padStart(2, '0');

    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');

    const formatted = `${year}-${month}-${day} ${hours}:${minutes}`;
    return formatted;
  }

  owner(): boolean {
    return this.account()?.login === this.profile()?.user?.login;
  }

  requestChat(id: number): void {
    this.subscribeToRequestChatResponse(this.profileService.requestChat(id));
    this.requested = true;
  }

  friends(): boolean {
    const inOthers = this.othersList().includes(this.account()!.login) || false;
    const inProfiles = this.profilesList().includes(this.account()!.login) || false;
    return inOthers || inProfiles;
  }

  friendsList(): IProfile[] {
    // Add profiles from others and profiles arrays
    this.addProfilesToMap(this.profile()?.others);
    this.addProfilesToMap(this.profile()?.profiles);

    const resultArray: IProfile[] = Array.from(this.map.values());
    return resultArray;
  }

  addProfilesToMap(profiles?: IProfile[] | null): void {
    if (!profiles) return;

    profiles.forEach(profile => {
      const login = profile.user?.login;
      if (login) {
        this.map.set(login, profile);
      }
    });
  }

  protected othersList(): string[] {
    return (
      this.profile()
        ?.others?.map(profile => profile.user?.login)
        .filter((login): login is string => !!login) ?? []
    );
  }

  protected profilesList(): string[] {
    return (
      this.profile()
        ?.profiles?.map(profile => profile.user?.login)
        .filter((login): login is string => !!login) ?? []
    );
  }

  protected subscribeToRequestChatResponse(result: Observable<HttpResponse<IChat>>): void {
    result.subscribe({
      next: res => this.onSaveSuccess(res.body!),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(updatedChat: IChat): void {
    //
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }
}
