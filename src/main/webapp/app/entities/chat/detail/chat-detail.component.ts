import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IChat } from '../chat.model';

@Component({
  selector: 'jhi-chat-detail',
  templateUrl: './chat-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ChatDetailComponent {
  chat = input<IChat | null>(null);

  previousState(): void {
    window.history.back();
  }
}
