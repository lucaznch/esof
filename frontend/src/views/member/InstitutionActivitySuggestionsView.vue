<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="activitySuggestions"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="institutionActivitySuggestionsTable"
    >
      <template v-slot:item.institutionName="{ item }">
        {{ institutionName() }}
      </template>

      <template v-slot:item.actions="{ item }">
        <div class="d-flex align-center" style="gap: 8px;">
          <v-btn
            v-if="item.state === 'IN_REVIEW'"
            icon
            color="green"
            @click="approve(item)"
            data-cy="approveActivitySuggestion"
          >
            <v-icon>mdi-thumb-up</v-icon>
          </v-btn>

          <v-btn
            v-if="item.state === 'IN_REVIEW' || item.state === 'APPROVED'"
            icon
            color="red"
            @click="reject(item)"
            data-cy="rejectActivitySuggestion"
          >
            <v-icon>mdi-thumb-down</v-icon>
          </v-btn>

          <v-btn
            v-if="item.state === 'REJECTED'"
            icon
            color="green"
            @click="approve(item)"
          >
            <v-icon>mdi-thumb-up</v-icon>
          </v-btn>
        </div>
      </template>

      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
        </v-card-title>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Institution from '@/models/institution/Institution';
import ActivitySuggestion from '@/models/activitysuggestion/ActivitySuggestion';


@Component({
  components: {
  },
})
export default class VolunteerActivitySuggestionsView extends Vue {
  activitySuggestions: ActivitySuggestion[] = [];
  institution: Institution = new Institution();
  search: string = '';

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
    {
      text: 'Actions',
      value: 'actions',
      align: 'left',
      width: '5%',
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      let userId = this.$store.getters.getUser.id;
      this.institution = await RemoteServices.getInstitution(userId);
      const institutionId = this.institution.id;
      if (!institutionId) {
        throw new Error('Institution ID not found');
      }
      this.activitySuggestions = await RemoteServices.getActivitySuggestionsByInstitution(institutionId);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  institutionName() {
    return this.institution.name;
  }

  async approve(item: ActivitySuggestion) {
    try {
      if (!item.id) { throw new Error('Activity suggestion ID not found'); }
      if (!this.institution.id) { throw new Error('Institution ID not found'); }
      await RemoteServices.approveActivitySuggestion(item.id, this.institution.id);
      item.state = 'APPROVED';
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async reject(item: ActivitySuggestion) {
    try {
      if (!item.id) { throw new Error('Activity suggestion ID not found'); }
      if (!this.institution.id) { throw new Error('Institution ID not found'); }
      await RemoteServices.rejectActivitySuggestion(item.id, this.institution.id);
      item.state = 'REJECTED';
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
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