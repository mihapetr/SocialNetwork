import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IChat, NewChat } from '../chat.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IChat for edit and NewChatFormGroupInput for create.
 */
type ChatFormGroupInput = IChat | PartialWithRequiredKeyOf<NewChat>;

type ChatFormDefaults = Pick<NewChat, 'id' | 'accepted' | 'profiles'>;

type ChatFormGroupContent = {
  id: FormControl<IChat['id'] | NewChat['id']>;
  initiatorName: FormControl<IChat['initiatorName']>;
  accepted: FormControl<IChat['accepted']>;
  user: FormControl<IChat['user']>;
  profiles: FormControl<IChat['profiles']>;
};

export type ChatFormGroup = FormGroup<ChatFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ChatFormService {
  createChatFormGroup(chat: ChatFormGroupInput = { id: null }): ChatFormGroup {
    const chatRawValue = {
      ...this.getFormDefaults(),
      ...chat,
    };
    return new FormGroup<ChatFormGroupContent>({
      id: new FormControl(
        { value: chatRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      initiatorName: new FormControl(chatRawValue.initiatorName),
      accepted: new FormControl(chatRawValue.accepted),
      user: new FormControl(chatRawValue.user),
      profiles: new FormControl(chatRawValue.profiles ?? []),
    });
  }

  getChat(form: ChatFormGroup): IChat | NewChat {
    return form.getRawValue() as IChat | NewChat;
  }

  resetForm(form: ChatFormGroup, chat: ChatFormGroupInput): void {
    const chatRawValue = { ...this.getFormDefaults(), ...chat };
    form.reset(
      {
        ...chatRawValue,
        id: { value: chatRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ChatFormDefaults {
    return {
      id: null,
      accepted: false,
      profiles: [],
    };
  }
}
