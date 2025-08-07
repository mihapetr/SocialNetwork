import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProfile, NewProfile } from '../profile.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProfile for edit and NewProfileFormGroupInput for create.
 */
type ProfileFormGroupInput = IProfile | PartialWithRequiredKeyOf<NewProfile>;

type ProfileFormDefaults = Pick<NewProfile, 'id' | 'others' | 'chats' | 'profiles'>;

type ProfileFormGroupContent = {
  id: FormControl<IProfile['id'] | NewProfile['id']>;
  status: FormControl<IProfile['status']>;
  picture: FormControl<IProfile['picture']>;
  pictureContentType: FormControl<IProfile['pictureContentType']>;
  user: FormControl<IProfile['user']>;
  others: FormControl<IProfile['others']>;
  chats: FormControl<IProfile['chats']>;
  profiles: FormControl<IProfile['profiles']>;
};

export type ProfileFormGroup = FormGroup<ProfileFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProfileFormService {
  createProfileFormGroup(profile: ProfileFormGroupInput = { id: null }): ProfileFormGroup {
    const profileRawValue = {
      ...this.getFormDefaults(),
      ...profile,
    };
    return new FormGroup<ProfileFormGroupContent>({
      id: new FormControl(
        { value: profileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      status: new FormControl(profileRawValue.status),
      picture: new FormControl(profileRawValue.picture),
      pictureContentType: new FormControl(profileRawValue.pictureContentType),
      user: new FormControl(profileRawValue.user),
      others: new FormControl(profileRawValue.others ?? []),
      chats: new FormControl(profileRawValue.chats ?? []),
      profiles: new FormControl(profileRawValue.profiles ?? []),
    });
  }

  getProfile(form: ProfileFormGroup): IProfile | NewProfile {
    return form.getRawValue() as IProfile | NewProfile;
  }

  resetForm(form: ProfileFormGroup, profile: ProfileFormGroupInput): void {
    const profileRawValue = { ...this.getFormDefaults(), ...profile };
    form.reset(
      {
        ...profileRawValue,
        id: { value: profileRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProfileFormDefaults {
    return {
      id: null,
      others: [],
      chats: [],
      profiles: [],
    };
  }
}
