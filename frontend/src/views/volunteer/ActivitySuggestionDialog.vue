<template>
    <v-dialog v-model="dialog" persistent width="1300">
      <v-card>
        <v-card-title>
          <span class="headline">
            {{
              editActivitySuggestion && editActivitySuggestion.id === null
                ? 'New Activity Suggestion'
                : 'Edit Activity Suggestion'
            }}
          </span>
        </v-card-title>
        <v-card-text>
          <v-form ref="form" lazy-validation>
            <v-row>
              <v-col cols="12" sm="6" md="4">
                <v-text-field
                  label="*Name"
                  :rules="[(v) => !!v || 'Activity name is required']"
                  required
                  v-model="editActivitySuggestion.name"
                  data-cy="nameInput"
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  label="*Region"
                  :rules="[(v) => !!v || 'Region name is required']"
                  required
                  v-model="editActivitySuggestion.region"
                  data-cy="regionInput"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6" md="4">
                <v-text-field
                  label="*Number of Participants"
                  :rules="[
                    (v) =>
                      isNumberValid(v) ||
                      'Number of participants between 1 and 5 is required',
                  ]"
                  required
                  v-model="editActivitySuggestion.participantsNumberLimit"
                  data-cy="participantsNumberInput"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-select
                  label="Institution"
                  v-model="selectedInstitutions"
                  :items="institutions"
                  multiple
                  return-object
                  item-text="name"
                  item-value="id"
                  required
                />
              </v-col>
              <v-col cols="12">
                <v-text-field
                  label="*Description"
                  :rules="[(v) => !!v || 'Description is required']"
                  required
                  v-model="editActivitySuggestion.description"
                  data-cy="descriptionInput"
                ></v-text-field>
              </v-col>
              <v-col>
                <VueCtkDateTimePicker
                  id="applicationDeadlineInput"
                  v-model="editActivitySuggestion.applicationDeadline"
                  format="YYYY-MM-DDTHH:mm:ssZ"
                  label="*Application Deadline"
                ></VueCtkDateTimePicker>
              </v-col>
              <v-col>
                <VueCtkDateTimePicker
                  id="startingDateInput"
                  v-model="editActivitySuggestion.startingDate"
                  format="YYYY-MM-DDTHH:mm:ssZ"
                  label="*Starting Date"
                ></VueCtkDateTimePicker>
              </v-col>
              <v-col>
                <VueCtkDateTimePicker
                  id="endingDateInput"
                  v-model="editActivitySuggestion.endingDate"
                  format="YYYY-MM-DDTHH:mm:ssZ"
                  label="*Ending Date"
                ></VueCtkDateTimePicker>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="blue-darken-1"
            variant="text"
            @click="$emit('close-activity-suggestion-dialog')"
          >
            Close
          </v-btn>
          <v-btn
            color="blue-darken-1"
            variant="text"
            @click="registerActivitySuggestion"
            :disabled="!canSave"
            data-cy="saveActivity"
          >
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </template>
  <script lang="ts">
  import { Vue, Component, Prop, Model } from 'vue-property-decorator';
  import ActivitySuggestion from '@/models/activitysuggestion/ActivitySuggestion';
  import RemoteServices from '@/services/RemoteServices';
  import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
  import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
  import { ISOtoString } from '@/services/ConvertDateService';
  import Institution from '@/models/institution/Institution';
  
  Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
  @Component({
    methods: { ISOtoString },
  })
  export default class ActivityDialog extends Vue {
    @Model('dialog', Boolean) dialog!: boolean;
    @Prop({ type: ActivitySuggestion, required: true }) readonly activitySuggestion!: ActivitySuggestion;
    @Prop({ type: Array, required: true }) readonly institutions!: Institution[]; // this is not used in the template, but it is passed as a prop
  
    editActivitySuggestion: ActivitySuggestion = new ActivitySuggestion();
    selectedInstitutions: Institution[] = [];
  
    cypressCondition: boolean = false;
  
    async created() {
      this.editActivitySuggestion = new ActivitySuggestion(this.activitySuggestion);
    }
  
    isNumberValid(value: any) {
      if (!/^\d+$/.test(value)) return false;
      const parsedValue = parseInt(value);
      return parsedValue >= 1 && parsedValue <= 5;
    }
  
    get canSave(): boolean {
      return (
        this.cypressCondition ||
        (!!this.editActivitySuggestion.name &&
          !!this.editActivitySuggestion.region &&
          !!this.editActivitySuggestion.participantsNumberLimit &&
          !!this.editActivitySuggestion.description &&
          !!this.editActivitySuggestion.startingDate &&
          !!this.editActivitySuggestion.endingDate &&
          !!this.editActivitySuggestion.applicationDeadline) &&
          this.selectedInstitutions.length > 0
      );
    }
  
    async registerActivitySuggestion() {
      if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
        try {
          const institutionId = this.selectedInstitutions[0].id;
          if (!institutionId) {
            throw new Error('Institution ID is required');
          }
          this.editActivitySuggestion.institutionId = institutionId;
          const result = await RemoteServices.createActivitySuggestion(institutionId, this.editActivitySuggestion);
          this.$emit('save-activity-suggestion', result);
        }
        catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    }

    
  }
  </script>
  
  <style scoped lang="scss"></style>
  