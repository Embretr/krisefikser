<script setup lang="ts">
import { ref, computed } from 'vue';
import { useForm } from 'vee-validate';
import * as z from 'zod';
import { toTypedSchema } from '@vee-validate/zod';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Home, MapPin, Mail, Building, Globe } from 'lucide-vue-next';

// Props
const props = defineProps({
  colorTheme: {
    type: String,
    default: 'blue',
    validator: (value: string) => ['blue', 'yellow', 'green'].includes(value)
  }
});

// Emits
const emit = defineEmits(['submit', 'cancel']);

// Schema
const formSchema = toTypedSchema(
  z.object({
    name: z.string()
      .min(2, 'Husstandsnavnet må være minst 2 tegn')
      .max(50, 'Husstandsnavnet må være mindre enn 50 tegn'),
    address: z.string()
      .min(5, 'Adressen må være minst 5 tegn')
      .max(100, 'Adressen må være mindre enn 100 tegn'),
    postalCode: z.string()
      .min(4, 'Postnummer må være minst 4 tegn')
      .max(5, 'Postnummer kan ikke være mer enn 5 tegn')
      .regex(/^\d+$/, 'Postnummer må være et tall'),
    city: z.string()
      .min(2, 'By/sted må være minst 2 tegn')
      .max(50, 'By/sted må være mindre enn 50 tegn'),
    country: z.string()
      .min(2, 'Land må være minst 2 tegn')
      .max(50, 'Land må være mindre enn 50 tegn')
      .default('Norge')
  })
);

// Initialize form
const { handleSubmit, meta } = useForm({
  validationSchema: formSchema,
  initialValues: {
    name: '',
    address: '',
    postalCode: '',
    city: '',
    country: 'Norge'
  },
  validateOnMount: false,
});

// Input restrictions
const onPostalCodeInput = (e: Event) => {
  const t = e.target as HTMLInputElement;
  t.value = t.value.replace(/\D/g, '');
};
const onPostalCodeKeyPressStrict = (e: KeyboardEvent) => {
  if (!/\d/.test(e.key)) e.preventDefault();
};
const onOnlyLetters = (e: KeyboardEvent) => {
  if (!/^[A-Za-zÆØÅæøå\s-]$/.test(e.key)) e.preventDefault();
};

// Submission
const loading = ref(false);
const onSubmit = handleSubmit((values) => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
    emit('submit', values);
  }, 500);
});

const buttonColorClass = computed(() => {
  const colors: Record<string,string> = {
    blue: 'bg-blue-600 hover:bg-blue-700',
    yellow: 'bg-yellow-600 hover:bg-yellow-700',
    green: 'bg-green-600 hover:bg-green-700'
  };
  return colors[props.colorTheme];
});

const isFormVisible = ref(true);
</script>

<template>
  <div class="new-household-container">
    <transition name="fade">
      <Card v-if="isFormVisible" class="w-full max-w-lg mx-auto shadow-lg">
        <CardHeader>
          <CardTitle class="text-2xl font-bold text-center">Opprett ny husstand</CardTitle>
          <CardDescription class="text-center">Fyll inn detaljene for din nye husstand</CardDescription>
        </CardHeader>

        <CardContent>
          <form @submit.prevent="onSubmit" class="space-y-6" novalidate>

            <FormField name="name" v-slot="{ field, meta }">
              <FormItem>
                <FormLabel>Husstandsnavn</FormLabel>
                <FormControl>
                  <div class="relative">
                    <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                      <Home class="h-5 w-5 text-gray-400" />
                    </div>
                    <Input v-bind="field" id="name" placeholder="Skriv inn husstandsnavn" class="pl-10" />
                  </div>
                </FormControl>
                <FormMessage v-slot="{ message }">
                  <p v-if="meta.touched && field.value">{{ message }}</p>
                </FormMessage>
              </FormItem>
            </FormField>

            <FormField name="address" v-slot="{ field, meta }">
              <FormItem>
                <FormLabel>Adresse</FormLabel>
                <FormControl>
                  <div class="relative">
                    <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                      <MapPin class="h-5 w-5 text-gray-400" />
                    </div>
                    <Input v-bind="field" id="address" placeholder="Skriv inn adresse" class="pl-10" />
                  </div>
                </FormControl>
                <FormMessage v-slot="{ message }">
                  <p v-if="meta.touched && field.value">{{ message }}</p>
                </FormMessage>
              </FormItem>
            </FormField>

            <div class="grid grid-cols-2 gap-4">

              <FormField name="postalCode" v-slot="{ field, meta }">
                <FormItem>
                  <FormLabel>Postnummer</FormLabel>
                  <FormControl>
                    <div class="relative">
                      <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                        <Mail class="h-5 w-5 text-gray-400" />
                      </div>
                      <Input
                        v-bind="field"
                        id="postalCode"
                        placeholder="0123"
                        class="pl-10"
                        inputmode="numeric"
                        pattern="[0-9]*"
                        @input="onPostalCodeInput"
                        @keypress="onPostalCodeKeyPressStrict"
                      />
                    </div>
                  </FormControl>
                  <FormMessage v-slot="{ message }">
                    <p v-if="meta.touched && field.value">{{ message }}</p>
                  </FormMessage>
                </FormItem>
              </FormField>

              <FormField name="city" v-slot="{ field, meta }">
                <FormItem>
                  <FormLabel>By/sted</FormLabel>
                  <FormControl>
                    <div class="relative">
                      <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                        <Building class="h-5 w-5 text-gray-400" />
                      </div>
                      <Input
                        v-bind="field"
                        id="city"
                        placeholder="Oslo"
                        class="pl-10"
                        @keypress="onOnlyLetters"
                      />
                    </div>
                  </FormControl>
                  <FormMessage v-slot="{ message }">
                    <p v-if="meta.touched && field.value">{{ message }}</p>
                  </FormMessage>
                </FormItem>
              </FormField>

            </div>

            <FormField name="country" v-slot="{ field, meta }">
              <FormItem>
                <FormLabel>Land</FormLabel>
                <FormControl>
                  <div class="relative">
                    <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                      <Globe class="h-5 w-5 text-gray-400" />
                    </div>
                    <Input
                      v-bind="field"
                      id="country"
                      placeholder="Norge"
                      class="pl-10"
                      @keypress="onOnlyLetters"
                    />
                  </div>
                </FormControl>
                <FormMessage v-slot="{ message }">
                  <p v-if="meta.touched && field.value">{{ message }}</p>
                </FormMessage>
              </FormItem>
            </FormField>

          </form>
        </CardContent>

        <CardFooter class="flex justify-end space-x-3">
          <Button variant="outline" type="button" @click="emit('cancel')">Avbryt</Button>
          <Button
            type="submit"
            @click="onSubmit"
            :disabled="!meta.valid"
            :class="buttonColorClass"
          >
            <span v-if="loading" class="mr-2">
              <svg class="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
              </svg>
            </span>
            {{ loading ? 'Lagrer...' : 'Opprett husstand' }}
          </Button>
        </CardFooter>
      </Card>
    </transition>
  </div>
</template>

