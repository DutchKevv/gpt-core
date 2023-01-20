import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StoryPageComponent } from './story-page.component';

const routes: Routes = [
  {
    path: '',
    component: StoryPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class StoryPageRoutingModule {}
