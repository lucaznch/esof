<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="activitySuggestions"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="volunteerActivitySuggestionsTable"
    >
      <template v-slot:item.institutionName="{ item }">
        {{ getInstitutionName(item.institutionId) }}
      </template>
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
          <v-btn color="primary" dark @click="newActivitySuggestion" data-cy="newActivitySuggestion"
            >New Activity Suggestion</v-btn
          >
        </v-card-title>
      </template>
    </v-data-table>

    <activitysuggestion-dialog
      v-if="currentActivitySuggestion && editActivitySuggestionDialog"
      v-model="editActivitySuggestionDialog"
      :activity-suggestion="currentActivitySuggestion"
      :institutions="institutions"
      v-on:save-activity-suggestion="onSaveActivitySuggestion"
      v-on:close-activity-suggestion-dialog="onCloseActivitySuggestionDialog"
    />

  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ActivitySuggestion from '@/models/activitysuggestion/ActivitySuggestion';
import Institution from '@/models/institution/Institution';
import ActivitySuggestionDialog from '@views/volunteer/ActivitySuggestionDialog.vue';

@Component({
  components: {
    'activitysuggestion-dialog': ActivitySuggestionDialog,
  },
})
export default class VolunteerActivitySuggestionsView extends Vue {
  activitySuggestions: ActivitySuggestion[] = [];
  institutions: Institution[] = [];
  search: string = '';
  
  currentActivitySuggestion: ActivitySuggestion | null = null;
  editActivitySuggestionDialog: boolean = false;
  suspendActivitySuggestionDialog: boolean = false;


  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Institution',
      value: 'institutionName',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Description',
      value: 'description',
      align: 'left',
      width: '30%',
    },
    {
      text: 'Region',
      value: 'region',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Participants Limit',
      value: 'participantsNumberLimit',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Start Date',
      value: 'formattedStartingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'End Date',
      value: 'formattedEndingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Application Deadline',
      value: 'formattedApplicationDeadline',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'State',
      value: 'state',
      align: 'left',
      width: '5%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      let userId = this.$store.getters.getUser.id;
      this.activitySuggestions = await RemoteServices.getActivitySuggestionsByVolunteer(userId);  // obtain all the activity suggestions for the logged-in volunteer
      this.institutions = await RemoteServices.getInstitutions(); // obtain all the registered institutions in the system
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  newActivitySuggestion() {
    this.currentActivitySuggestion = new ActivitySuggestion();
    this.editActivitySuggestionDialog = true;
  }

  editActivitySuggestion(activitySuggestion: ActivitySuggestion) {
    this.currentActivitySuggestion = activitySuggestion;
    this.editActivitySuggestionDialog = true;
  }

  onCloseActivitySuggestionDialog() {
    this.currentActivitySuggestion = null;
    this.editActivitySuggestionDialog = false;
  }

  onSaveActivitySuggestion(activitySuggestion: ActivitySuggestion) {
    this.activitySuggestions.unshift(activitySuggestion);
    this.currentActivitySuggestion = null;
    this.editActivitySuggestionDialog = false;
  }

  getInstitutionName(institutionId: number): string {
    const institution = this.institutions.find((inst) => inst.id === institutionId);
    return institution ? institution.name : 'Unknown Institution';
  }
}
</script>

<style lang="scss" scoped>
.date-fields-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.date-fields-row {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
</style>